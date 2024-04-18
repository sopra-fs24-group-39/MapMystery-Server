package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
// import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
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
  private LobbyService lobbyService;

  @MockBean
  private UserService userService;

  @Mock
  private SimpMessagingTemplate messagingTemplate;
  @InjectMocks
  private Lobby lob = new Lobby(messagingTemplate);

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
    given(lobbyService.putToSomeLobby(user)).willReturn(1L);

    // when
    MockHttpServletRequestBuilder postRequest = post("/Lobby")
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
    given(lobbyService.putToSomeLobby(any())).willReturn(-1L);

    // when
    MockHttpServletRequestBuilder postRequest = post("/Lobby")
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
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    user.setPassword("null");
    user.setScore(40);
    long userId = 1L; // Assuming IDs are long
    user.setId(userId);
    String token = user.getToken();
    Long lobbyId = 1L;
    lob.addPlayer(user);
    lob.addPlayer(user);
    lob.addPlayer(user);

    // Mocking UserService to return a specific user when getUser() is called
    given(lobbyService.getLobby(1L)).willReturn(lob);

    // when
    MockHttpServletRequestBuilder request = put("/Lobby/{lobbyId}",lobbyId)
    .header("Authorization",token)
    .content(new ObjectMapper().writeValueAsString(user))
    .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(request)
        .andExpect(status().isOk());
  }

  @Test
  public void testSendGuessFAilure() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    user.setPassword("null");
    user.setScore(40);
    long userId = 1L; // Assuming IDs are long
    user.setId(userId);
    String token = user.getToken();
    Long lobbyId = 1L;
    lob.addPlayer(user);
    lob.addPlayer(user);

    // Mocking UserService to return a specific user when getUser() is called
    given(lobbyService.getLobby(1L)).willReturn(null);

    // when
    MockHttpServletRequestBuilder request = put("/Lobby/{lobbyId}",lobbyId)
    .header("Authorization",token)
    .content(new ObjectMapper().writeValueAsString(user))
    .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(request)
        .andExpect(status().isInternalServerError());
  }

}