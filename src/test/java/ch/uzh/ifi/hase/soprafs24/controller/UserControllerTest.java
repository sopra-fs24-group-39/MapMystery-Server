package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CredPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
//import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
// import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.AccountService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.UtilityService;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
// import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
// import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private AccountService accountService;

  @MockBean
  private UtilityService utilityService;


  @Test
  public void testGET_users_userid_success() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    user.setPassword("null");
    long userId = 1L; // Assuming IDs are long
    user.setId(userId);
    String token = user.getToken();

    // Mocking UserService to return a specific user when getUser() is called
    given(userService.getUser(userId)).willReturn(user);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/{userId}", userId)
    .header("Authorization",token)
    .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest)
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus())))
        .andExpect(status().isOk());
  }

  @Test
  public void testGET_users_userid_failure() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    long userId = 1L;
    String token = user.getToken();

    user.setId(userId);

    // Mocking UserService to return a specific user when getUser() is called
    given(userService.getUser(userId)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found by id"));

    // when
    MockHttpServletRequestBuilder Request = get("/users/{userId}", userId)
    .header("Authorization",token)
    .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(Request)
        .andExpect(status().isNotFound());
  }

  @Test
  public void testPUT_users_userid_success() throws Exception {
      // Given
      long userId = 1L;

      UserPutDTO updatedUserData = new UserPutDTO();
      updatedUserData.setUsername("updatedUser");
      updatedUserData.setPassword("newPassword");


      User existingUser = new User();
      existingUser.setId(userId);
      existingUser.setUsername("existingUser");
      existingUser.setStatus("OFFLINE");
      String token = existingUser.getToken();

      given(userService.getUser(userId)).willReturn(existingUser);
      // No need to mock userService.updateUser() as it does not return a value, but ensure it does not throw exceptions

      MockHttpServletRequestBuilder Request = put("/users/{userId}", userId)
        .header("Authorization",token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(updatedUserData));

      // When & Then
      mockMvc.perform(Request)
              .andExpect(status().isNoContent());
  }

  @Test
  public void testPUT_users_userid_failure() throws Exception {
      // Given
      long userId = 1L;
      UserPutDTO updatedUserData = new UserPutDTO();
      updatedUserData.setUsername("updatedUser");
      updatedUserData.setPassword("newPassword");
      // updatedUserData.setUserId(0);


      User existingUser = new User();
      existingUser.setId(userId);
      existingUser.setUsername("existingUser");
      existingUser.setStatus("OFFLINE");
      String token = existingUser.getToken();

      given(userService.getUser(userId)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found by id"));
      // No need to mock userService.updateUser() as it does not return a value, but ensure it does not throw exceptions

      MockHttpServletRequestBuilder Request = put("/users/{userId}", userId)
        .header("Authorization",token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(updatedUserData));

      // When & Then
      mockMvc.perform(Request)
              .andExpect(status().isNotFound());
  }

  @Test
  public void testPOST_users_success() throws Exception {
    // Given
    CredPostDTO newUser = new CredPostDTO();
    newUser.setUsername("newUser");
    newUser.setPassword("password");
    newUser.setUserEmail("unique@example.com"); // Provide a unique email

    // Mock the behavior of the accountService.sendVerificationEmail method
    doNothing().when(accountService).sendVerificationEmail(any(User.class));

    // When & Then
    mockMvc.perform(post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(new ObjectMapper().writeValueAsString(newUser)))
      .andExpect(status().isCreated());
  }

  @Test
  public void testPOST_users_failure() throws Exception {
    // Given
    CredPostDTO newUser = new CredPostDTO();
    newUser.setUsername("newUser");
    newUser.setPassword("password");

    given(userService.createUser(any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT,"user not found by id"));

    // When & Then
    mockMvc.perform(post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(new ObjectMapper().writeValueAsString(newUser)))
      .andExpect(status().isConflict());
  }

  @Test
  public void testGET_Users_success() throws Exception {
    // given
    User user1 = new User();
    user1.setUsername("firstname@lastname");
    user1.setStatus("OFFLINE");

    User user2 = new User();
    user2.setUsername("secondname@lastname");
    user2.setStatus("ONLINE");

    List<User> allUsers = Arrays.asList(user1, user2);

    // Mocking UserService to return a list of users when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].username", is(user1.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user1.getStatus())))
        .andExpect(jsonPath("$[1].username", is(user2.getUsername())))
        .andExpect(jsonPath("$[1].status", is(user2.getStatus())));
  }


  @Test
    public void testDELETE_users_userid_success() throws Exception {
        // Given
        long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("existingUser");
        existingUser.setStatus("OFFLINE");
        String token = existingUser.getToken();

        given(userService.getUser(userId)).willReturn(existingUser);

        MockHttpServletRequestBuilder Request = delete("/users/{userId}", userId)
          .header("Authorization",token)
          .contentType(MediaType.APPLICATION_JSON);

        // When & Then
        mockMvc.perform(Request)
                .andExpect(status().isOk());
    }

    @Test
    public void testDELETE_users_userid_failure() throws Exception {
        // Given
        long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("existingUser");
        existingUser.setStatus("OFFLINE");
        String token = existingUser.getToken();

        given(userService.getUser(userId)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found by id"));

        MockHttpServletRequestBuilder Request = delete("/users/{userId}", userId)
          .header("Authorization",token)
          .contentType(MediaType.APPLICATION_JSON);

        // When & Then
        mockMvc.perform(Request)
                .andExpect(status().isNotFound());
    }

}