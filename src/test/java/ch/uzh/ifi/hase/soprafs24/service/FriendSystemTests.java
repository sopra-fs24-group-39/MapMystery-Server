package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FriendSystemTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void acceptFriendRequest_success() throws Exception {
        User receiver = new User();
        receiver.setUsername("receiver");
        receiver.setFriendrequests(new ArrayList<>(List.of("sender")));
        receiver.setFriends(new ArrayList<>());

        User sender = new User();
        sender.setUsername("sender");
        sender.setFriends(new ArrayList<>());

        when(userRepository.findByUsername("receiver")).thenReturn(receiver);
        when(userRepository.findByUsername("sender")).thenReturn(sender);

        userService.acceptfriendrequest(receiver, sender);

        verify(userRepository, times(1)).save(receiver);
        verify(userRepository, times(1)).save(sender);
        assertTrue(receiver.getFriends().contains("sender"));
        assertTrue(sender.getFriends().contains("receiver"));
        assertFalse(receiver.getFriendrequests().contains("sender"));
    }

    @Test
    public void declineFriendRequest_success() throws Exception {
        User receiver = new User();
        receiver.setUsername("receiver");
        receiver.setFriendrequests(new ArrayList<>(List.of("sender")));

        User sender = new User();
        sender.setUsername("sender");

        when(userRepository.findByUsername("receiver")).thenReturn(receiver);
        when(userRepository.findByUsername("sender")).thenReturn(sender);

        userService.declinefriendrequest(receiver, sender);

        verify(userRepository, times(1)).save(receiver);
        assertFalse(receiver.getFriendrequests().contains("sender"));
    }

    @Test
    public void removeFriend_success() throws Exception {
        User friend1 = new User();
        friend1.setUsername("friend1");
        friend1.setFriends(new ArrayList<>(List.of("friend2")));

        User friend2 = new User();
        friend2.setUsername("friend2");
        friend2.setFriends(new ArrayList<>(List.of("friend1")));

        when(userRepository.findByUsername("friend1")).thenReturn(friend1);
        when(userRepository.findByUsername("friend2")).thenReturn(friend2);

        userService.removefriendship(friend1, friend2);

        verify(userRepository, times(1)).save(friend1);
        verify(userRepository, times(1)).save(friend2);
        assertFalse(friend1.getFriends().contains("friend2"));
        assertFalse(friend2.getFriends().contains("friend1"));
    }
    @Test
    public void testAddFriendRequest_UserNotExists() throws Exception {
        User sender = new User();
        sender.setUsername("sender");

        when(userRepository.findByUsername("nonexistentUser")).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> userService.addfriendrequest(new User(), sender));
    }

    @Test
    public void testRemoveFriend_UserNotInFriendList() throws Exception {
        User friend1 = new User();
        friend1.setUsername("friend1");
        friend1.setFriends(new ArrayList<>()); // No friends initially

        User friend2 = new User();
        friend2.setUsername("friend2");
        friend2.setFriends(new ArrayList<>(List.of("friend1"))); // friend2 has friend1, but friend1 doesn't have friend2

        when(userRepository.findByUsername("friend1")).thenReturn(friend1);
        when(userRepository.findByUsername("friend2")).thenReturn(friend2);

        assertThrows(ResponseStatusException.class, () -> userService.removefriendship(friend1, friend2));
    }

}
