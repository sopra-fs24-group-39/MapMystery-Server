package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.*;

import org.hibernate.internal.ExceptionConverterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.constants.GameModes;
import ch.uzh.ifi.hase.constants.lobbyStates;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class LobbyService {
    private int LobbyLimit = 10;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private RestTemplate restTemplate = new RestTemplateBuilder().build();
    private GameService gameService = new GameService(restTemplate);

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    UserRepository userRepository;


    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private UtilityService util;


    /*GETTERS AND SETTERS #######################################################################################################################33 */

    public List<Lobby> getAllLobbies() {
        return this.lobbyRepository.findAll();
    }


    public void setLobbyLimit(int limit) {
        this.LobbyLimit = limit;
    }

    // Auth Key Gen for private lobbies
    public String generateAuthKey() {
        return UUID.randomUUID().toString();
    }

    public Long getLobbyCount() {
        return this.lobbyRepository.count();
    }

    public int getLobbyLimit() {
        return this.LobbyLimit;
    }

    public Lobby getLobby(Long lobbyId) throws Exception {
        try {
            Lobby foundLobby = this.lobbyRepository.findByLobbyId(lobbyId);
            util.Assert(foundLobby != null, "no Lobby found with LobbyId: " + lobbyId);
            return foundLobby;

        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found with given ID");
        }
    }

    /*FUNCTIONS FOR COMMUNICATION #######################################################################################################################33 */

    /*
     * prepares coordinates for sending to client
     */
    private Map<String, String> createCoordResp(List<Double> coordinates) throws Exception {
        try {
            Map<String, String> response = new HashMap<>();

            response.put(coordinates.get(0).toString(), "longitude");
            response.put(coordinates.get(1).toString(), "lattitude");
            return response;

        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Async
    public void createSendTaskCoord(Lobby lob, Long miliseconds) throws Exception {
        taskScheduler.schedule(() -> {
            try {
                sendCoord(lob.getId());
            }
            catch (Exception e) {
                lob.setState(lobbyStates.CLOSED);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }, new Date(System.currentTimeMillis() + miliseconds));
    }

    @Async
    public void createSendTaskLeaderB(Lobby lob, Long miliseconds) throws Exception {
        taskScheduler.schedule(() -> {
            try {
                createAndSendLeaderBoard(lob);
            }
            catch (Exception e) {
                lob.setState(lobbyStates.CLOSED);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }, new Date(System.currentTimeMillis() + miliseconds));
    }

    @Async
    public boolean createKickOutInactivePlayers(Lobby lob, int roundToCheck) throws Exception {
        taskScheduler.schedule(() -> {
            try {
                util.Assert(lob != null, "provided lobby was null");
                this.kickOutInactivePlayers(lob.getId(), roundToCheck);
            }
            catch (Exception e) {
                lob.setLobbyState(lobbyStates.CLOSED);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }, new Date(System.currentTimeMillis() + lob.getRoundDuration() + 800L));
        return true;
    }

    @Async
    public void createAndSendLeaderBoard(Lobby lob) throws Exception {
        try {
            Map<Long, Float> results = lob.getPoints();
            List<User> players = lob.getPlayers();
            Map<String, Float> response = new HashMap<>();

            for (int k = 0; k < players.size(); k++) {
                User player = players.get(k);
                float score = results.getOrDefault(player.getId(), 0.0f);
                response.put(player.getUsername(), score);
                score += player.getCurrentpoints();
                player.setCurrentpoints(score);

                float this_month_score = results.getOrDefault(player.getId(), 0.0f);
                this_month_score += player.getPointsthismonth();
                player.setPointsthismonth(this_month_score);


            }
            this.messagingTemplate.convertAndSend(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lob.getId()), response);

        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "could not create and send Leaderboard");
        }


    }

    /*
     * sets the distance achieved in lobby and advancesRound for user
     * then checks if all players are ready for next round notifies them with next
     * coordinates
     */
    public void submitScore(float distance, float timeDelta, Long userId, Lobby lob) throws Exception {
        lob.setPoints(distance, timeDelta, userId);
        this.advanceRound(userId, lob);
        lobbyRepository.saveAndFlush(lob);

        boolean nextRound = this.checkNextRound(lob);
        this.triggerNextRound(nextRound, lob);

    }

    @Async
    public void sendCoord(Long lobbyId) throws Exception {
        List<Double> coord = this.gameService.get_image_coordinates();
        Map<String, String> resp = this.createCoordResp(coord);
        this.messagingTemplate.convertAndSend(String.format("/topic/lobby/GameMode1/coordinates/%s", lobbyId), resp);
    }

    /*FUNCTIONS FOR CREATING AND DELETING LOBBIES OF DIFFERENT KINDS#######################################################################################################################33 */

    public void refreshLobbies() {
        List<Lobby> lobbies = this.getAllLobbies();

        for (int k = 0; k < lobbies.size(); k++) {
            Lobby lob = lobbies.get(k);
            // we also remove and reinitialize lobbies here!!
            if (lob.getState() == lobbyStates.CLOSED) {
                lob.players = null;
                lobbyRepository.delete(lob);
            }
        }
        lobbyRepository.flush();

    }

    public Lobby createLobby(GameModes gamemode) throws Exception {
        Lobby lob;
        if (gamemode == GameModes.Gamemode1) {
            lob = new GameMode1();
        }
        else if (gamemode == GameModes.Gamemode3) {
            lob = new GameMode3();
        }
        else {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "no valid gamemode");
        }

        this.lobbyRepository.saveAndFlush(lob);

        return lob;
    }

    public Lobby createPrivateLobby(GameModes gamemode) throws Exception {
        Lobby lob;
        if (gamemode == GameModes.Gamemode1) {
            lob = new GameMode1();
            lob.setPrivate();
            lob.setAuthKey(generateAuthKey());
        }
        else {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "no valid gamemode");
        }
        this.lobbyRepository.saveAndFlush(lob);
        return lob;
    }


    /*FUNCTIONS FOR JOINING LOBBIES OF DIFFERENT KINDS#######################################################################################################################33 */

    /**
     * puts the user to some open lobby for the correct gammode
     *
     * @param user
     * @return lobbyId if successful, -1 if not open lobby was found
     */
    public Long putToSomeLobby(User user, GameModes gamemode) throws Exception {
        // this.messagingTemplate.convertAndSend("/topic/lobby/2","hello there");
        this.refreshLobbies();
        List<Lobby> lobbies = this.getAllLobbies();

        for (int k = 0; k < lobbies.size(); k++) {
            Lobby lob = lobbies.get(k);
            // Only iterate through public lobbies
            if (lob.getState() == lobbyStates.OPEN && lob.getGamemode() == gamemode && lob.isPublic()) {
                try {
                    if (this.isPlayerInLobby(user, lob.getId())) {
                        return lob.getId();
                    }
                    this.joinLobby(user, lob, null);
                    return lob.getId();
                }
                catch (Exception e) {
                    continue;
                }
            }
        }

        if (this.LobbyLimit > lobbies.size()) {
            Lobby lob = this.createLobby(gamemode);
            this.joinLobby(user, lob, null);
            return lob.getId();

        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "could not put player to some lobby");
    }

    /**
     * @param user the user who wants to join the lobby
     * @param lob  the lobby, assumes lobby to be open
     * @return the lobby state after adding the user
     */
    public lobbyStates joinLobby(User user, Lobby lob, String providedAuthKey) throws Exception {

        // Check for private lobby
        if (!lob.isPublic()) {
            if (providedAuthKey == null || providedAuthKey.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please provide an authentication key to join the private lobby");
            }
            if (!lob.getAuthKey().equals(providedAuthKey)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please provide a valid auth key to join the private lobby");
            }

        }

        this.addPlayer(user, lob);

        if (lob.getPlayers().size() == lob.getPlayerLimit()) {
            lob.setState(lobbyStates.PLAYING);
            this.triggerNextRound(true, lob);
            return lobbyStates.PLAYING;
        }
        else {
            return lob.getState();
        }
    }

    /**
     * @param user the player to add to the lobby
     * @param lob  the lobby
     * @return lobbyId if successfull -1 otherwise
     * @throws Exception
     */
    public Long addPlayer(User user, Lobby lob) throws Exception {
        try {
            int numberOfMembers = lob.players.size();

            if (numberOfMembers < lob.getPlayerLimit()) {
                lob.players.add(user);
                lob.currRound.put(user.getId(), 0);
                lob.setPoints(-1, 0, user.getId());
                lobbyRepository.saveAndFlush(lob);
                this.messagingTemplate.convertAndSend(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lob.getId()), user.getUsername() + " just joined the lobby");

                return lob.getId();
            }
            else {
                return -1L;
            }

        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not add player: " + user.getUsername() + " to the lobby with Id: " + lob.getId());
        }

    }

    public void removePlayer(User user, Lobby lob) throws Exception {
        try {
            boolean removed = lob.players.remove(user);
            lobbyRepository.saveAndFlush(lob);
            if (!removed) {
                throw new Exception("User not found in players list");
            }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not remove player: " + user.getUsername() + " to the lobby with Id: " + lob.getId());
        }
    }


    /*FUNCTIONS FOR MANAGING THE GAME STATE#######################################################################################################################33 */
    public boolean kickOutInactivePlayers(Long lobbyId, int roundToCheck) throws Exception {
        try {
            Lobby lob = lobbyRepository.findByLobbyId(lobbyId);
            List<User> tobeDel = new ArrayList<>();
            for (User player : lob.players) {
                Long playerId = player.getId();
                if (!lob.currRound.containsKey(playerId) || lob.currRound.get(playerId) < roundToCheck) {
                    tobeDel.add(player);
                }
            }
            // delete players afterwards to avoid concurrency errors
            for (User player : tobeDel) {
                this.removePlayer(player, lob);
            }
            if (tobeDel.size() > 0) {
                lobbyRepository.saveAndFlush(lob);
                boolean nextRound = this.checkNextRound(lob);

                this.triggerNextRound(nextRound, lob);
            }

            return true;

        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not kick out inactive players from the lobby with Id: " + lobbyId);
        }
    }

    /*
     * after settting the state to finished
     * it sends the results to all players
     */
    public void endGame(Lobby lob) throws Exception {
        try {
            for (User user : lob.getPlayers()) {
                resetPrivateLobbyOwnerStatus(user);
            }
            lob.setState(lobbyStates.CLOSED);
            createAndSendLeaderBoard(lob);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not end game properly with lobbyId: " + lob.getId());
        }
    }

    public void triggerNextRound(boolean nextRound, Lobby lob) throws Exception {
        try {
            if (nextRound && lob.getState() == lobbyStates.PLAYING) {
                int NextPlayingRound = lob.getPlayingRound();

                if (NextPlayingRound < lob.getRounds()) {
                    lob.setPlayingRound(NextPlayingRound + 1);
                    lobbyRepository.saveAndFlush(lob);
                }
                this.createSendTaskCoord(lob, 6000L);
                this.createSendTaskLeaderB(lob, 2000L);
                this.createKickOutInactivePlayers(lob, lob.getPlayingRound());

            }
        }
        catch (Exception e) {
            lob.setState(lobbyStates.CLOSED);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not initialize next round: " + lob.getId());
        }
    }

    public void resetPrivateLobbyOwnerStatus(User user) throws Exception {
        if (user.isPrivateLobbyOwner()) {
            user.setPrivateLobbyOwner(false);
            userRepository.saveAndFlush(user);
        }
    }


    public void advanceRound(Long playerId, Lobby lob) throws Exception {
        try {
            int oldRound = lob.currRound.get(playerId);
            if (oldRound < lob.getRounds()) {
                lob.currRound.put(playerId, ++oldRound);
            }
            boolean gameEnded = checkGameState(lob);

            if (gameEnded) {
                this.endGame(lob);
            }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not advance round at lobby with lobbyId: " + lob.getId());
        }

    }


    /*
     * checks if all the players have advanced to the same round which is true
     * iff when all players have submitted their geuss for the previous round
     */
    public boolean checkGameState(Lobby lob) throws Exception {
        try {
            for (int k = 0; k < lob.players.size(); k++) {
                Long playerId = lob.players.get(k).getId();
                if (lob.currRound.get(playerId) < lob.getRounds()) {
                    return false;
                }
            }
            return true;

        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not check game state at lobby with lobbyId: " + lob.getId());
        }
    }

    public boolean checkNextRound(Lobby lob) throws Exception {
        try {
            int playingRound = lob.getPlayingRound();
            if (lob.players.size() != 0) {

                for (int k = 0; k < lob.players.size(); k++) {
                    Long playerId = lob.players.get(k).getId();
                    int currentRound = lob.currRound.get(playerId);

                    if (playingRound != currentRound) {
                        return false;
                    }

                }
                return true;
            }
            return false;

        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not check next round at lobby with lobbyId: " + lob.getId());
        }

    }

    public boolean hasExistingPrivateLobby(User user) throws Exception {
        return false;
    }

    public boolean isPlayerInLobby(User player, Long lobbyID) throws Exception {
        try {
            Lobby lobby = getLobby(lobbyID);
            List<User> players = lobby.getPlayers();
            return players.contains(player);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to check if player is in the lobby: " + e.getMessage());
        }


    }
}
