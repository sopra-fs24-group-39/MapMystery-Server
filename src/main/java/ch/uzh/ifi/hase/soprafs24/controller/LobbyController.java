/**
 * Author: David Sanchez
 */


package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.*;
import ch.uzh.ifi.hase.constants.GameModes;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameCountryService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GuessResultPutDTO;
import ch.uzh.ifi.hase.soprafs24.entity.GuessResult;
import ch.uzh.ifi.hase.constants.lobbyStates;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@RestController
public class LobbyController {
 
  private final UserService userService;
  private final LobbyService lobbyService;
  private final GameCountryService gameCountryService;

  @Autowired
  private UtilityService util;

     public LobbyController(UserService userService, LobbyService lobbyService, GameCountryService gameCountryService) {
         this.userService = userService;
         this.lobbyService = lobbyService;
         this.gameCountryService = gameCountryService;
     }
 
  /**
  * @param UserData is the User object with all of its attributes
  * please ensure that its really all attributes
  * @return returns only the status codes
  */


  @PostMapping("/Lobby/GameMode1")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Map<String, Long> joinLobby(@RequestBody UserPutDTO userData, @RequestHeader(value = "Authorization") String token) throws Exception {
      User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userData);
      User player = userService.getUser(user.getId());

      // Check for token
      validateToken(player, token);

      String authKey = userData.getAuthKey();

      // Differentiate between a public join and a private join
      if (isAuthKeyValid(authKey)) {
          return joinPrivateLobby(player, userData, authKey);
      } else {
          return joinPublicLobby(player);
      }
  }

  private void validateToken(User player, String token) throws Exception {
      util.Assert(player.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");
  }

  private boolean isAuthKeyValid(String authKey) {
      return authKey != null && !authKey.isEmpty();
  }

  private Map<String, Long> joinPrivateLobby(User player, UserPutDTO userData, String authKey) throws Exception {
      Long lobbyID = userData.getLobbyID();
      Lobby lobby = lobbyService.getLobby(lobbyID);

      if (lobbyService.isPlayerInLobby(player, lobbyID)) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player is already in a lobby.");
      }

      if(!Objects.equals(lobby.getAuthKey(), authKey)){
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized to join this lobby, bad auth key");
      }

      lobbyService.joinLobby(player, lobby, authKey);

      Map<String, Long> response = new HashMap<>();
      response.put("lobbyId", lobbyID);
      return response;
}

  private Map<String, Long> joinPublicLobby(User player) throws Exception {
      Long lobbyId = lobbyService.putToSomeLobby(player, GameModes.Gamemode1);

      Map<String, Long> response = new HashMap<>();
      response.put("lobbyId", lobbyId);
      return response;
  }


  @PostMapping("/Lobby/private/GameMode1")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Map<String, Object> createPrivateLobby(@RequestBody UserPutDTO UserData,@RequestHeader(value = "Authorization") String token) throws Exception {
    User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(UserData);
    User player = userService.getUser(user.getId());
    util.Assert(player.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");
    System.out.println(player.isPrivateLobbyOwner());


    // Create Private Lobby we need to return the lobbyId and authKey so the player can share it
    Lobby newLobby = lobbyService.createPrivateLobby(GameModes.Gamemode1);
    String authKey = newLobby.getAuthKey();

    //if (player.isPrivateLobbyOwner()) {
    //    throw new IllegalStateException("User already has an existing private lobby.");
    //

    //player.setPrivateLobbyOwner(true);

    userService.updateUser(player, player);

    lobbyService.joinLobby(player, newLobby, authKey);


    Map<String, Object> response = new HashMap<>();
    response.put("lobbyId", newLobby.getId());
    response.put("authKey", authKey);
    return response;
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
  public void sendGuess(@PathVariable long lobbyId,@RequestBody GuessResultPutDTO UserData,@RequestHeader(value = "Authorization") String token) throws Exception {
    GuessResult results = DTOMapper.INSTANCE.convertGuessResultDTOtoEntity(UserData);
    Long userId = results.getPlayerId();
    User player = userService.getUser(userId);
    util.Assert(player.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");
  
    float distance = results.getDistance();
    float timeDelta = results.getTimeDelta();

    Lobby lob = lobbyService.getLobby(lobbyId);

    util.Assert(lobbyService.isPlayerInLobby(player,lob.getId()), "User is not in said lobby");
    util.Assert(lob.getState()==lobbyStates.PLAYING, "That lobby is not playing anymore");
    
    lobbyService.submitScore(distance,timeDelta, userId, lob);
    
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
  public void leaveLobby(@PathVariable long lobbyId,@PathVariable long userId,@RequestHeader(value = "Authorization") String token) throws Exception{
    User user = userService.getUser(userId);
    util.Assert(user.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");
  
    Lobby lob = lobbyService.getLobby(lobbyId);
    util.Assert(lob.getGamemode()==GameModes.Gamemode1,"wrong gamemode for this endpoint");

    lobbyService.removePlayer(user, lob);
    
  }



  /*CONTROLLER FOR GAMEMMODE3################################################################################################## */
  @PostMapping("/Lobby/GameMode3")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Map<String, Long> joinLobby3(@RequestBody UserPutDTO UserData,@RequestHeader(value = "Authorization") String token) throws Exception{
    User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(UserData);
    User player = userService.getUser(user.getId());

    util.Assert(player.getToken().equals(token), "the provided token did not match the token expected in the Usercontroller");    


    Long lobbyId = lobbyService.putToSomeLobby(player,GameModes.Gamemode3);

    Map<String,Long> response = new HashMap<>();

    response.put("lobbyId", lobbyId);
    return response;
  }
  
    @GetMapping("Lobby/GameMode2/country")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, String> get_country(@RequestHeader(value = "Authorization") String token){

        Map<String, String> country = gameCountryService.randomCountry();
        Map<String, String> response =  new HashMap<>();
        response.put("country", country.get("name"));
        response.put("code", country.get("code"));
        return response;
    }
  
}

