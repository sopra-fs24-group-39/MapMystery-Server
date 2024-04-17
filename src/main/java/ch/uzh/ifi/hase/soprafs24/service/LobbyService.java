package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
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

  private final LobbyRepository lobbyRepository;

  @Autowired
  public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository) {
    this.lobbyRepository = lobbyRepository;
  }

  public List<Lobby> getAllLobbies() {
    return this.lobbyRepository.findAll();
  }

  public Lobby getLobby(Long lobbyId){
    return this.lobbyRepository.findByLobbyId(lobbyId);
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

  /**
   * puts the user to some open lobby
  * @param user
  * @return
  */
  public Long putToSomeLobby(User user){
    this.refreshLobbies();
    List<Lobby> lobbies = this.getAllLobbies();
    for ( int k = 0 ; k< this.LobbyLimit;k++){
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
