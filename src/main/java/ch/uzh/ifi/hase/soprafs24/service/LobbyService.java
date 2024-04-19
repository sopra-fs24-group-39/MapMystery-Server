package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.constants.lobbyStates;

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
  private int LobbyLimit = 5;
  private final Logger log = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @Autowired
  private LobbyRepository lobbyRepository;

  public List<Lobby> getAllLobbies() {
    return this.lobbyRepository.findAll();
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

  public void addPlayer(User user,Lobby lob) throws Exception{
 
    int numberOfMembers = lob.players.size();

    if (numberOfMembers < lob.getPlayerLimit() ) {
      lob.players.add(user);
      lob.currRound.put(user.getId(),1);
      lob.distances.put(user.getId(),0);
    } 
    
    if (lob.getState() == lobbyStates.OPEN && numberOfMembers +1 >= lob.getPlayerLimit()) {
      lob.setState(lobbyStates.PLAYING);
      try{
       //  initGame();
      }
      catch (Exception e){
        throw new Exception(e.getMessage());
      }
    }
  } 

  public void removePlayer(User user,Lobby lob) throws Exception {
    try {
      boolean removed = lob.players.remove(user);
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


  public void endGame(Lobby lob){
    lob.setState(lobbyStates.FINISHED);
  }

  public void advanceRound(Long playerId, Lobby lob){
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
  private boolean checkGameState(Lobby lob){
    for (int k = 0; k < lob.players.size(); k++){ 
      Long playerId = lob.players.get(k).getId();
      if(lob.currRound.get(playerId) <5){
        return false;
      }
    }
    this.endGame(lob);
    return true;
  }

  private void refreshLobbies(){
    List<Lobby> lobbies = this.getAllLobbies();
    
    for( int k = 0; k < lobbies.size();k++){
      Lobby lob = lobbies.get(k);
      // we also remove and reinitialize lobbies here!!
      if(lob.getState() == lobbyStates.FINISHED){
        lobbyRepository.delete(lob);
      }
    }
    lobbyRepository.flush();
      
  }

  public Lobby createLobby(){
    Lobby lob = new Lobby();
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
   * puts the user to some open lobby
  * @param user
  * @return lobbyId if successful, -1 if not open lobby was found
  */
  public Long putToSomeLobby(User user){
    this.refreshLobbies();
    List<Lobby> lobbies = this.getAllLobbies();
    for ( int k = 0 ; k< lobbies.size();k++){
      Lobby lob = lobbies.get(k);

      if(lob.getState() == lobbyStates.OPEN){
        try{
          this.addPlayer(user,lob);
          lob.currRound.put(user.getId(), 1);
          lobbyRepository.saveAndFlush(lob);
          return lob.getId();
        }
        catch (Exception e){
          continue;
        }
      }
    }
    return -1L;
  }



}
