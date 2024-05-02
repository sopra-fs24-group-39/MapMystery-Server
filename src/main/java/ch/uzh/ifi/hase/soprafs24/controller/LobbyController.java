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
  private UtilityService util;

 
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
      User player = userService.getUser(user.getId());


      // CHeck if the lobby is private and if the auth key matches
      String authKey = UserData.getAuthKey();
      if(authKey != null && !authKey.isEmpty()){
          //Get Lobby ID from User

          Long lobbyID = UserData.getLobbyID();
          Lobby lobby = lobbyService.getLobby(lobbyID);
          lobbyService.joinLobby(player, lobby, authKey);
      }
      util.Assert(player.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");

      Long lobbyId = lobbyService.putToSomeLobby(player,GameModes.Gamemode1);

      Map<String,Long> response = new HashMap<>();

      response.put("lobbyId", lobbyId);
      return response;



    } 
    catch (AssertionError e){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
    }
    catch (RuntimeException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
    catch (Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage() );
    }
     
   }
   @PutMapping("/Lobby/private/GameMode1")
   @ResponseStatus(HttpStatus.OK)
   @ResponseBody
   public Map<String, Object> createPrivateLobby(@RequestBody UserPutDTO UserData,@RequestHeader(value = "Authorization") String token) {
       try {
           User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(UserData);
           User player = userService.getUser(user.getId());
           util.Assert(player.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");


           // Create Private Lobby we need to return the lobbyId and authKey so the player can share it
           Lobby newLobby = lobbyService.createPrivateLobby(GameModes.Gamemode1);
           String authKey = newLobby.getAuthKey();

           Map<String, Object> response = new HashMap<>();
           response.put("lobbyId", newLobby.getId());
           response.put("authKey", authKey);
           return response;
       } catch (AssertionError e) {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
       } catch (RuntimeException e) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
       } catch (Exception e) {
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
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
       User player = userService.getUser(user.getId());
       util.Assert(player.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");
      
       Long userId = user.getId();
       float score = user.getScore();

       Lobby lob = lobbyService.getLobby(lobbyId);
       
       lobbyService.submitScore(score, userId, lob);
 
     } 
     catch (AssertionError e){
       throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
     }
     catch (RuntimeException e){
       throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
     }
     catch (Exception e){
       throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage() );
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
       util.Assert(user.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");
      
       Lobby lob = lobbyService.getLobby(lobbyId);
       
       lobbyService.removePlayer(user, lob);
 
     } 
     catch (AssertionError e){
       throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
     }
     catch (RuntimeException e){
       throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
     }
     catch (Exception e){
       throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage() );
     }
      
    }
   
 }

 