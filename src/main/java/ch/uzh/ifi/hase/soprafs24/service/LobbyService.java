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
    * @index needs to be less than amount of rounds
    */
   private Map<String,String> createCoordResp(int index, Lobby lob){
    Map<String, String> response = new HashMap<>();

    response.put(lob.coordinates.get(index).get(0).toString(),"longitude");
    response.put(lob.coordinates.get(index).get(1).toString(),"lattitude");
    return response;
   }

  private void initGame(Lobby lob) throws  Exception{
    String topic = String.format("/topic/Lobby/%s", lob.getId());
    Map<String, String> response = this.createCoordResp(0,lob);

    messagingTemplate.convertAndSend(topic,response);
   }

  public Lobby getLobby(Long lobbyId){
    return this.lobbyRepository.findByLobbyId(lobbyId);
  }

  public void advanceRound(Long playerId, Lobby lob){
    int oldRound = lob.currRound.get(playerId);
    if(oldRound < lob.rounds){
      lob.currRound.put(playerId, ++oldRound);
    }

    if (this.checkGameState(lob)){
      lob.endGame();
    }
    else if (lob.checkNextRound()){
      // send the cooordinates to each player
      
    }

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
      return true;
    }

  private void refreshLobbies(){
    List<Lobby> lobbies = this.getAllLobbies();
    
    for( int k = 0; k < lobbies.size();k++){
      Lobby lob = lobbies.get(k);
      // we also remove and reinitialize lobbies here!!
      if(lob.getState() == "closed"){
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

      if(lob.getState() == "open"){
        try{
          lob.addPlayer(user);
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
