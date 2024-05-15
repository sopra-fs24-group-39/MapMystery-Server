package ch.uzh.ifi.hase.soprafs24.controller;

import aj.org.objectweb.asm.TypeReference;
import ch.uzh.ifi.hase.constants.lobbyStates;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameCountryService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.UtilityService;
import io.jsonwebtoken.lang.Assert;

import org.apache.tomcat.util.http.parser.Authorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
// import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.constants.GameModes;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ch.uzh.ifi.hase.soprafs24.entity.GuessResult;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GuessResultPutDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private LobbyService lobbyService;

  @MockBean
  private UtilityService utilityService;

  @MockBean
  private GameCountryService gameCountryService;

  private Lobby lob;

  @BeforeEach
  public void setup(){
    lob = new Lobby();
  }

    @Test
    public void testJoinPrivateLobbySuccess() throws Exception {
        // given
        User user = new User();
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setStatus("OFFLINE");
        long userId = 1L;
        user.setId(userId);
        String token = user.getToken();
        String authKey = "HELLO WORLD";
        Lobby newLobby = lobbyService.createPrivateLobby(GameModes.Gamemode1);

        // Mocking the joinLobby method to return the state of the lobby
        given(lobbyService.joinLobby(any(User.class), eq(newLobby), eq(authKey))).willReturn(lobbyStates.PLAYING);
        given(userService.getUser(1L)).willReturn(user);

        // when
        MockHttpServletRequestBuilder postRequest = post("/Lobby/GameMode1")
                .header("Authorization", token)
                .content(new ObjectMapper().writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk());
    }


    @Test
  public void testJoinLobbySuccess() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    user.setPassword("null");
    long userId = 1L; // Assuming IDs are long
    user.setId(userId);
    String token = user.getToken();

    // Mocking UserService to return a specific user when getUser() is called
    given(lobbyService.putToSomeLobby(user,GameModes.Gamemode1)).willReturn(1L);
    given(userService.getUser(1L)).willReturn(user);


    // when
    MockHttpServletRequestBuilder postRequest = post("/Lobby/GameMode1")
    .header("Authorization",token)
    .content(new ObjectMapper().writeValueAsString(user))
    .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isOk());
  }

  @Test
  public void testJoinLobbyFailure() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    user.setPassword("null");
    long userId = 1L; // Assuming IDs are long
    user.setId(userId);
    String token = user.getToken();

    // Mocking UserService to return a specific user when getUser() is called
    given(lobbyService.putToSomeLobby(any(),any())).willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"could not put to some lobby"));

    given(userService.getUser(1L)).willReturn(user);

    // when
    MockHttpServletRequestBuilder postRequest = post("/Lobby/GameMode1")
    .header("Authorization",token)
    .content(new ObjectMapper().writeValueAsString(user))
    .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void testSendGuessSuccess() throws Exception {
    // given

    GuessResult result = new GuessResult();
    result.setDistance(40);
    result.setTimeDelta(0);
    result.setPlayerId(1L);
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    user.setPassword("null");
    user.setScore(40);
    long userId = 1L; // Assuming IDs are long
    user.setId(userId);
    String token = user.getToken();
    Long lobbyId = 1L;
    lobbyService.addPlayer(user,lob);
    lobbyService.addPlayer(user,lob);
    lobbyService.addPlayer(user,lob);

    // Mocking UserService to return a specific user when getUser() is called
    given(lobbyService.getLobby(1L)).willReturn(lob);
    given(userService.getUser(1L)).willReturn(user);

    // when
    MockHttpServletRequestBuilder request = put("/Lobby/GameMode1/{lobbyId}",lobbyId)
    .header("Authorization",token)
    .content(new ObjectMapper().writeValueAsString(result))
    .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(request)
        .andExpect(status().isOk());
  }

  @Test
  public void testSendGuessFAilure() throws Exception {
    // given
    GuessResult result = new GuessResult();
    result.setDistance(40);
    result.setTimeDelta(0);
    result.setPlayerId(1L);
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    user.setPassword("null");
    user.setScore(40);
    long userId = 1L; // Assuming IDs are long
    user.setId(userId);
    String token = user.getToken();
    Long lobbyId = 1L;
    lobbyService.addPlayer(user,lob);
    lobbyService.addPlayer(user,lob);

    // Mocking UserService to return a specific user when getUser() is called
    given(lobbyService.getLobby(1L)).willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    given(userService.getUser(1L)).willReturn(user);

    // when
    MockHttpServletRequestBuilder request = put("/Lobby/GameMode1/{lobbyId}",lobbyId)
    .header("Authorization",token)
    .content(new ObjectMapper().writeValueAsString(result))
    .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(request)
        .andExpect(status().isInternalServerError());
  }

  @Test
  public void testLeaveLobbySuccess() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    user.setPassword("null");
    long userId = 1L; // Assuming IDs are long
    user.setId(userId);
    String token = user.getToken();

    lobbyService.addPlayer(user,lob);
    Long lobbyId = 1L;

    List<User> expectedPlayers = new ArrayList<>();


    // Mocking UserService to return a specific user when getUser() is called
    given(userService.getUser(userId)).willReturn(user);
    given(lobbyService.getLobby(lobbyId)).willReturn(lob);


    // when
    MockHttpServletRequestBuilder Request = delete("/Lobby/GameMode1/{lobbyId}/{userId}",lobbyId,userId)
    .header("Authorization",token)
    .content(new ObjectMapper().writeValueAsString(user))
    .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(Request)
        .andExpect(status().isOk());

    assertEquals(lob.players,expectedPlayers );
  }

  @Test
  public void testLeaveLobbyFailure() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    user.setPassword("null");
    long userId = 1L; // Assuming IDs are long
    user.setId(userId);
    String token = user.getToken();

    Long lobbyId = 1L;

    // Mocking UserService to return a specific user when getUser() is called
    given(userService.getUser(userId)).willReturn(user);
    given(lobbyService.getLobby(lobbyId)).willThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    // when
    MockHttpServletRequestBuilder Request = delete("/Lobby/GameMode1/{lobbyId}/{userId}",lobbyId,userId)
    .header("Authorization",token)
    .content(new ObjectMapper().writeValueAsString(user))
    .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(Request)
        .andExpect(status().isInternalServerError());
  }



    @Test
    public void testGetCountrySuccess() throws Exception {
        // given
        String token = "validToken";
        Map<String, String> mockCountry = new HashMap<>();
        mockCountry.put("name", "Switzerland");
        mockCountry.put("code", "CH");

        given(gameCountryService.randomCountry()).willReturn(mockCountry);

        // when
        MockHttpServletRequestBuilder getRequest = get("/Lobby/GameMode2/country")
                .header("Authorization", token);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("Switzerland"))
                .andExpect(jsonPath("$.code").value("CH"));
    }


}