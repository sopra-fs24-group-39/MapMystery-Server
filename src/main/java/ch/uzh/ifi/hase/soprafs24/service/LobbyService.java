package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.*;
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
import org.springframework.boot.web.client.RestTemplateBuilder;

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

  
  public void setLobbyLimit(int limit){
    this.LobbyLimit = limit;
  }

  // Auth Key Gen for private lobbies
  public String generateAuthKey(){
    return UUID.randomUUID().toString();
  }

  public Long getLobbyCount(){
    return this.lobbyRepository.count();
}

  public int getLobbyLimit(){
    return this.LobbyLimit;
  }

  public Lobby getLobby(Long lobbyId) throws Exception{
    Lobby foundLobby =  this.lobbyRepository.findByLobbyId(lobbyId);
    util.Assert(foundLobby != null, "no Lobby found with LobbyId: "+lobbyId);
    return foundLobby;
  }

/*FUNCTIONS FOR COMMUNICATION #######################################################################################################################33 */

  /*
  * prepares coordinates for sending to client 
  */
  private Map<String,String> createCoordResp( List<Double> coordinates){
    Map<String, String> response = new HashMap<>();

    response.put(coordinates.get(0).toString(),"longitude");
    response.put(coordinates.get(1).toString(),"lattitude");
    return response;
  }

  @Async
  public void createSendTaskCoord(Lobby lob,Long miliseconds) throws Exception{
    taskScheduler.schedule(() -> {
      try {
          sendCoord(lob.getId());
      } catch (Exception e) {
          lob.setState(lobbyStates.CLOSED);
          throw new RuntimeException(e.getMessage());
      }
    }, new Date(System.currentTimeMillis() + miliseconds));
  }
    
  @Async
  public void createSendTaskLeaderB(Lobby lob,Long miliseconds) throws Exception{ 
    taskScheduler.schedule(() -> {
      try {
          createAndSendLeaderBoard(lob);
      } catch (Exception e) {
          lob.setState(lobbyStates.CLOSED);
          throw new RuntimeException(e.getMessage());
      }
    }, new Date(System.currentTimeMillis() + miliseconds));
  }

  @Async
  public void createKickOutInactivePlayers(Lobby lob) throws Exception{
    taskScheduler.schedule(() -> {
      try {
        this.kickOutInactivePlayers(lob.getId());
      } catch (Exception e) {
          lob.setLobbyState(lobbyStates.CLOSED);
          throw new RuntimeException(e.getMessage());
      }
    }, new Date(System.currentTimeMillis() + lob.getRoundDuration()+800L));
  }

  @Async
  public void createAndSendLeaderBoard(Lobby lob) throws Exception{
   
    Map<Long,Float> results = lob.getPoints();
    List<User> players = lob.getPlayers();
    Map<String,Float> response = new HashMap<>();

    for(int k = 0; k < players.size();k++){
      User player = players.get(k);
      float score = results.getOrDefault(player.getId(),0.0f);
      response.put(player.getUsername(), score);
      score += player.getCurrentpoints();
      player.setCurrentpoints(score);
      userRepository.saveAndFlush(player);
    }
    this.messagingTemplate.convertAndSend(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lob.getId()),response);

    
  }

  /*
   * sets the distance achieved in lobby and advancesRound for user
   * then checks if all players are ready for next round notifies them with next
   * coordinates
   */
  public void submitScore(float distance,float timeDelta,Long userId,Lobby lob) throws Exception{
    lob.setPoints(distance,timeDelta, userId);
    this.advanceRound(userId,lob);
    boolean nextRound = this.checkNextRound(lob);
    lobbyRepository.saveAndFlush(lob);
    this.triggerNextRound(nextRound, lob);

  }

  @Async
  public void sendCoord(Long lobbyId) throws Exception{
    List<Double> coord = this.gameService.get_image_coordinates();
    Map<String,String> resp = this.createCoordResp(coord);
    this.messagingTemplate.convertAndSend(String.format("/topic/lobby/GameMode1/coordinates/%s", lobbyId),resp); 
  }

  /*FUNCTIONS FOR CREATING AND DELETING LOBBIES OF DIFFERENT KINDS#######################################################################################################################33 */

  public void refreshLobbies(){
    List<Lobby> lobbies = this.getAllLobbies();
    
    for( int k = 0; k < lobbies.size();k++){
      Lobby lob = lobbies.get(k);
      // we also remove and reinitialize lobbies here!!
      if(lob.getState() == lobbyStates.CLOSED){
        lob.players = null;
        lobbyRepository.delete(lob);
      }
    }
    lobbyRepository.flush();
      
  }

  public Lobby createLobby(GameModes gamemode) throws Exception{
    Lobby lob;
    if (gamemode == GameModes.Gamemode1){
      lob = new GameMode1();
    }
    else{
      throw new Exception("no valid gamemode");
    }

    this.lobbyRepository.saveAndFlush(lob);

    return lob;
  }

  public Lobby createPrivateLobby(GameModes gamemode) throws Exception{
      Lobby lob;
      if(gamemode == GameModes.Gamemode1){
          lob = new GameMode1();
          lob.setPrivate();
          lob.setAuthKey(generateAuthKey());
          }
      else{
          throw new Exception("no valid gamemode");
      }
      this.lobbyRepository.saveAndFlush(lob);
      return lob;
  }

  
   /*FUNCTIONS FOR JOINING LOBBIES OF DIFFERENT KINDS#######################################################################################################################33 */

  /**
   * puts the user to some open lobby for the correct gammode
  * @param user
  * @return lobbyId if successful, -1 if not open lobby was found
  */
  public Long  putToSomeLobby(User user,GameModes gamemode) throws Exception{
    // this.messagingTemplate.convertAndSend("/topic/lobby/2","hello there");
    this.refreshLobbies();
    List<Lobby> lobbies = this.getAllLobbies();

    for ( int k = 0 ; k< lobbies.size();k++){
      Lobby lob = lobbies.get(k);
      // Only iterate through public lobbies
      if(lob.getState() == lobbyStates.OPEN && lob.getGamemode() == gamemode && lob.isPublic()){
        try{
          this.joinLobby(user,lob,null);
          return lob.getId();
        }
        catch (Exception e){
          continue;
        }
      }
    }

    if ( this.LobbyLimit > lobbies.size()){
      Lobby lob = this.createLobby(gamemode);
      this.joinLobby(user, lob,null);
      return lob.getId();

    }
    throw new Exception("all lobbies are full");
  }
  
  /**    * 
     * @param user the user who wants to join the lobby
     * @param lob the lobby, assumes lobby to be open
     * @return the lobby state after adding the user
     */
  public lobbyStates joinLobby(User user, Lobby lob, String providedAuthKey) throws Exception{

    // Check for private lobby
    if(!lob.isPublic()){
        if(providedAuthKey == null || providedAuthKey.isEmpty()){
            throw new Exception("Please provide an authentication key to join the private lobby");
        }
        if(!lob.getAuthKey().equals(providedAuthKey)){
            throw new Exception("Invalid Authentication key");
        }

    }
    
    try{
      this.addPlayer(user, lob);
    }
    catch (Exception e){
      lob.setState(lobbyStates.CLOSED);
      throw new Exception("Player could not join lobby "+e.getMessage());
    }


    if ( lob.getPlayers().size() == lob.getPlayerLimit()){
      try{
        lob.setState(lobbyStates.PLAYING);
        this.triggerNextRound(true, lob);
        return lobbyStates.PLAYING;
      }
      catch (Exception e){
        lob.setState(lobbyStates.CLOSED);
        throw new Exception("Game could not initialize, create new lobby");
      }
      
    }
    else{
      return lob.getState();
    }
  }

  /**
   * 
   * @param user the player to add to the lobby
   * @param lob the lobby 
   * @return lobbyId if successfull -1 otherwise
   * @throws Exception
   */
  public Long addPlayer(User user,Lobby lob) {
 
    int numberOfMembers = lob.players.size();

    if (numberOfMembers < lob.getPlayerLimit() ) {
      lob.players.add(user);
      lob.currRound.put(user.getId(),0);
      lob.setPoints(-1,0, user.getId());
      lobbyRepository.saveAndFlush(lob);
      this.messagingTemplate.convertAndSend(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lob.getId()),user.getUsername()+" just joined the lobby");

      return lob.getId();
    } 
    else {
      return -1L;
    }
    
  } 

  public void removePlayer(User user,Lobby lob) throws Exception {
    try {
      boolean removed = lob.players.remove(user);
      lobbyRepository.saveAndFlush(lob);
      if (!removed) {
        throw new Exception("User not found in players list");
      }
    } catch (Exception e) {
        throw new Exception("Failed to remove players: " + e.getMessage());
      }
  }

  
  /*FUNCTIONS FOR MANAGING THE GAME STATE#######################################################################################################################33 */
  @Transactional
  public boolean kickOutInactivePlayers(Long lobbyId) throws Exception{
    Lobby lob = lobbyRepository.findByLobbyId(lobbyId);
    List<User> tobeDel = new ArrayList<>();
    for(User player : lob.players){
      Long playerId = player.getId();
      if (!lob.currRound.containsKey(playerId) || lob.currRound.get(playerId) < lob.getPlayingRound()) {
        tobeDel.add(player);
      }
    }
    // delete players afterwards to avoid concurrency errors
    for(User player:tobeDel){
      this.removePlayer(player, lob);
    }
    boolean nextRound = this.checkNextRound(lob);

    this.triggerNextRound(nextRound, lob);
    return true;
  }

  /*
   * after settting the state to finished
   * it sends the results to all players
   */
  public void endGame(Lobby lob) throws Exception{
      for (User user : lob.getPlayers()) {
          resetPrivateLobbyOwnerStatus(user);
      }
      lob.setState(lobbyStates.CLOSED);
    createAndSendLeaderBoard(lob);

  }

  public void triggerNextRound(boolean nextRound, Lobby lob) throws Exception{
    if(nextRound && lob.getState() == lobbyStates.PLAYING){
      try{
        this.createSendTaskCoord(lob,6000L);
        this.createSendTaskLeaderB(lob,2000L);
        // this.createKickOutInactivePlayers(lob);

        
        int NextPlayingRound = lob.getPlayingRound();

        if(NextPlayingRound < lob.getRounds()){
          lob.setPlayingRound(NextPlayingRound+1);
        }
      }
      catch (Exception e){
        lob.setState(lobbyStates.CLOSED);
        throw new Exception("Game could not initialize, create new lobby");
      }
    }
  }

  public void resetPrivateLobbyOwnerStatus(User user) throws Exception {
        if (user.isPrivateLobbyOwner()) {
            user.setPrivateLobbyOwner(false);
            userRepository.saveAndFlush(user);
        }
    }


  public void advanceRound(Long playerId, Lobby lob)throws Exception{
    int oldRound = lob.currRound.get(playerId);
    if(oldRound < lob.getRounds()){
      lob.currRound.put(playerId, ++oldRound);
    }


    boolean gameEnded = checkGameState(lob);
    
  }

  
  /*
  * checks if all the players have advanced to the same round which is true
  * iff when all players have submitted their geuss for the previous round
  */
  public boolean checkGameState(Lobby lob) throws Exception{
    for (int k = 0; k < lob.players.size(); k++){ 
      Long playerId = lob.players.get(k).getId();
      if(lob.currRound.get(playerId) <lob.getRounds()){
        return false;
      }
    }
    this.endGame(lob);
    return true;
  }

  public boolean checkNextRound(Lobby lob){
    if(lob.players.size()!= 0){
      Long playerId = lob.players.get(0).getId();
      int currentRound = lob.currRound.get(playerId);
  
      for (int k = 1; k < lob.players.size(); k++){ 
        playerId = lob.players.get(k).getId();
        int currentRound2 = lob.currRound.get(playerId);
  
        if(currentRound != currentRound2 ){
          return false;
        }
  
        currentRound = currentRound2;
      }
      return true;
    }
    return false;
    
  }

    public boolean hasExistingPrivateLobby(User user) {
      return false;
    }

    public boolean isPlayerInLobby(User player, Long lobbyID) {
        try {
            Lobby lobby = getLobby(lobbyID);
            List<User> players = lobby.getPlayers();
            return players.contains(player);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to check if player is in the lobby: " + e.getMessage(), e);
        }


    }
}
