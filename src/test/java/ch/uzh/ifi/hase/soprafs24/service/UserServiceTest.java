package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.List;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testUsername");
    testUser.setPassword("9Ko{$");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() throws Exception{
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals("OFFLINE", createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() throws Exception{
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() throws Exception{
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

    @Test
    public void testGetUserSuccess() throws Exception {
        // given
        String username = "testUsername";
        User user = new User();
        user.setUsername(username);

        given(userRepository.findByUsername(username)).willReturn(user);

        // when
        User foundUser = userService.getUser(username);

        // then
        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");

        // Mocking the repository to do nothing on delete
        doNothing().when(userRepository).delete(user);

        // when
        userService.deleteUser(user);

        // then
        verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    public void testUpdateUserSuccess() throws Exception {
        User existingUser = mock(User.class);
        User newUser = new User();
        newUser.setUsername("newUsername");
        newUser.setUserEmail("new@example.com");

        doNothing().when(existingUser).update(newUser);
        when(userRepository.saveAndFlush(existingUser)).thenReturn(existingUser);

        assertDoesNotThrow(() -> userService.updateUser(existingUser, newUser));

        verify(existingUser).update(newUser);
        verify(userRepository).saveAndFlush(existingUser);
    }

    @Test
    public void testUpdateUserThrowsException() {
        User existingUser = mock(User.class);
        User newUser = new User();
        newUser.setUsername("newUsername");
        newUser.setUserEmail("new@example.com");

        doThrow(new DataIntegrityViolationException("Unique constraint violation")).when(existingUser).update(newUser);

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            userService.updateUser(existingUser, newUser);
        });

        assertEquals(HttpStatus.CONFLICT, ((ResponseStatusException) exception).getStatus());
        assertTrue(exception.getMessage().contains("Could not update User"));

        verify(existingUser).update(newUser);
        verify(userRepository, never()).saveAndFlush(existingUser);
    }


    
    
    @Test
    public void testGetUsersThrowsException() throws Exception {
        // given
        given(userRepository.findAll()).willThrow(new RuntimeException("Database error"));
    
        // then
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            userService.getUsers();
        });
    
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception).getStatus());
        assertTrue(exception.getMessage().contains("Could not return all users from Userrepository"));
    }
    
    
    
    
    
    
    @Test
    public void testGetUserByVerificationTokenSuccess() throws Exception {
        // given
        String token = "verificationToken";
        User user = new User();
    
        given(userRepository.findByVerificationCode(token)).willReturn(user);
    
        // when
        User foundUser = userService.getUserByVerificationToken(token);
    
        // then
        assertNotNull(foundUser);
    }
    


}
