/**
 * Author: David Sanchez
 */


package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.*;
import ch.uzh.ifi.hase.constants.GameModes;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.HashMap;
import java.util.Map;
 
 
 @RestController
 public class LobbyController {
 
  private final UserService userService;
  private final LobbyService lobbyService;

  @Autowired
  private UtilityService utilityService;

 
  LobbyController(UserService userService,LobbyService lobbyService) {
    this.userService = userService;
    this.lobbyService = lobbyService;
   }
 
   /**
    * @param UserData is the User object with all of its attributes
    * please ensure that its really all attributes
    * @return returns only the status codes
    */
   @PostMapping("/Lobby/GameMode1")
   @ResponseStatus(HttpStatus.OK)
   @ResponseBody
   public Map<String, Long> joinLobby(@RequestBody UserPutDTO UserData,@RequestHeader(value = "Authorization") String token) {
 
    try{
      User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(UserData);
      assert user.getToken().equals(token);
      Long userId = user.getId();

      user = userService.getUser(userId);

      Long lobbyId = lobbyService.putToSomeLobby(user,GameModes.Gamemode1);

      if (lobbyId == -1){
        
        throw new Exception("all lobbies are full");
        
      }

      Map<String,Long> response = new HashMap<>();

      response.put("lobbyId", lobbyId);
      return response;



    } 
    catch (AssertionError e){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"token is invalid! "+e.getMessage());
    }
    catch (RuntimeException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with %d not found "+e.getMessage());
    }
    catch (Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected Error occured "+e.getMessage() );
    }
     
   }


   /**
    * GameId = LobbyId for simplicity
    * @param lobbyId is the Id of the lobby
    * @param UserData is the User object with all of its attributes is still nee
    * needed for security reasons
    * @return returns only the status codes
    */
    @PutMapping("/Lobby/GameMode1/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void sendGuess(@PathVariable long lobbyId,@RequestBody UserPutDTO UserData,@RequestHeader(value = "Authorization") String token) {
  
     try{
       User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(UserData);
       assert user.getToken().equals(token);
      
       Long userId = user.getId();
       float score = user.getScore();
       Lobby lob = lobbyService.getLobby(lobbyId);
       if( lob == null){
        throw new Exception("lobby not found");
       };
       lobbyService.submitScore(score, userId, lob);
 
     } 
     catch (AssertionError e){
       throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"token is invalid! "+e.getMessage());
     }
     catch (RuntimeException e){
       throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with %d not found "+e.getMessage());
     }
     catch (Exception e){
       throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected Error occured "+e.getMessage() );
     }
      
    }
 
    /**
    * GameId = LobbyId for simplicity
    * @param lobbyId is the Id of the lobby
    * @param userId is the Id of the user which leaves the loby
    * needed for security reasons
    * @return returns only the status codes
    */
    @DeleteMapping("/Lobby/GameMode1/{lobbyId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void leaveLobby(@PathVariable long lobbyId,@PathVariable long userId,@RequestHeader(value = "Authorization") String token) {
  
     try{
       User user = userService.getUser(userId);
       assert user.getToken().equals(token);
      
       Lobby lob = lobbyService.getLobby(lobbyId);
       if( lob == null){
        throw new Exception("lobby not found");
       };
       lobbyService.removePlayer(user, lob);
 
     } 
     catch (AssertionError e){
       throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"token is invalid! "+e.getMessage());
     }
     catch (RuntimeException e){
       throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with %d not found "+e.getMessage());
     }
     catch (Exception e){
       throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected Error occured "+e.getMessage() );
     }
      
    }
   
 }
 