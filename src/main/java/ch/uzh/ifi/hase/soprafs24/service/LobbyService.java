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
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private UtilityService utilityService;

  public List<Lobby> getAllLobbies() {
    return this.lobbyRepository.findAll();
  }

  public void setLobbyLimit(int limit){
    this.LobbyLimit = limit;
  }

  /*
    * prepares coordinates for sending to client 
    */
  private Map<String,String> createCoordResp( List<Double> coordinates){
    Map<String, String> response = new HashMap<>();

    response.put(coordinates.get(0).toString(),"longitude");
    response.put(coordinates.get(1).toString(),"lattitude");
    return response;
   }


  public Lobby getLobby(Long lobbyId){
    return this.lobbyRepository.findByLobbyId(lobbyId);
  }

  @Async
  public void createSendTaskCoord(Lobby lob,Long miliseconds){
    taskScheduler.schedule(() -> {
      try {
          sendCoord(lob.getId());
      } catch (Exception e) {
          lob.setState(lobbyStates.CLOSED);
          // handle exception
      }
    }, new Date(System.currentTimeMillis() + miliseconds));
  }
    
  @Async
  public void createSendTaskLeaderB(Lobby lob,Long miliseconds){
    taskScheduler.schedule(() -> {
      try {
          createAndSendLeaderBoard(lob);
      } catch (Exception e) {
          lob.setState(lobbyStates.CLOSED);
          // handle exception
      }
    }, new Date(System.currentTimeMillis() + miliseconds));
  }
 
  
  
  /**    * 
     * @param user the user who wants to join the lobby
     * @param lob the lobby, assumes lobby to be open
     * @return the lobby state after adding the user
     */
  public lobbyStates joinLobby(User user, Lobby lob) throws Exception{
    this.addPlayer(user, lob);

    if ( lob.getPlayers().size() == lob.getPlayerLimit()){
      try{
        lob.setState(lobbyStates.PLAYING);
        this.createSendTaskLeaderB(lob,2000L);
        this.createSendTaskCoord(lob, 6000L);
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
      lob.setPoints(-1, user.getId());
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

  public boolean checkNextRound(Lobby lob){
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

  @Async
  public void createAndSendLeaderBoard(Lobby lob){
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

    // TODO: seems not to work
    // this.refreshLobbies();

    this.messagingTemplate.convertAndSend(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lob.getId()),response);
  }

  /*
   * after settting the state to finished
   * it sends the results to all players
   */
  public void endGame(Lobby lob){
    lob.setState(lobbyStates.CLOSED);
    createAndSendLeaderBoard(lob);

  }


  public void advanceRound(Long playerId, Lobby lob)throws Exception{
    int oldRound = lob.currRound.get(playerId);
    if(oldRound < lob.getRounds()){
      lob.currRound.put(playerId, ++oldRound);
    }
    checkGameState(lob);
    
  }

  
  /*
  * checks if all the players have advanced to the same round which is true
  * iff when all players have submitted their geuss for the previous round
  */
  public boolean checkGameState(Lobby lob){
    for (int k = 0; k < lob.players.size(); k++){ 
      Long playerId = lob.players.get(k).getId();
      if(lob.currRound.get(playerId) <5){
        return false;
      }
    }
    this.endGame(lob);
    return true;
  }

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

  public Long getLobbyCount(){
    return this.lobbyRepository.count();
  }

  public int getLobbyLimit(){
    return this.LobbyLimit;
  }

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

      if(lob.getState() == lobbyStates.OPEN && lob.getGamemode() == gamemode){
        try{
          this.joinLobby(user,lob);
          return lob.getId();
        }
        catch (Exception e){
          continue;
        }
      }
    }

    if ( this.LobbyLimit > lobbies.size()){
      try{

        Lobby lob = this.createLobby(gamemode);
        this.joinLobby(user, lob);
        return lob.getId();
      }
      catch (Exception e){
        throw new Exception("User could not join Lobby even though spots are left");
      }

    }
    return -1L;
  }

  /*
   * sets the distance achieved in lobby and advancesRound for user
   * then checks if all players are ready for next round notifies them with next
   * coordinates
   */
  public void submitScore(float distance,Long userId,Lobby lob) throws Exception{
    lob.setPoints(distance, userId);
    this.advanceRound(userId,lob);
    boolean nextRound = this.checkNextRound(lob);

    if(nextRound && lob.getState() == lobbyStates.PLAYING){
      try{
        this.createSendTaskCoord(lob,6000L);
        this.createSendTaskLeaderB(lob,2000L);
      }
      catch (Exception e){
        lob.setState(lobbyStates.CLOSED);
        throw new Exception("Game could not initialize, create new lobby");
      }
    }

  }

  @Async
  public void sendCoord(Long lobbyId) throws Exception{
    try{
      List<Double> coord = this.gameService.get_image_coordinates();
      Map<String,String> resp = this.createCoordResp(coord);
      this.messagingTemplate.convertAndSend(String.format("/topic/lobby/GameMode1/coordinates/%s", lobbyId),resp);
    }
    catch (Exception e){
      throw new Exception("Game could not initialize, create new lobby");
    }
    
  }

}
