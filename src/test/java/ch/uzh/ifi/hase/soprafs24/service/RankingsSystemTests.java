package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RankingsSystemTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllUsersForRanking() throws Exception {
        // Create some sample users
        User user1 = new User();
        user1.setFeatured_in_rankings(true);

        User user2 = new User();
        user2.setFeatured_in_rankings(false);

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        // Mock the userRepository to return the sample users
        when(userRepository.findAll()).thenReturn(userList);

        // Call the method under test
        List<User> rankedUsers = userService.getUsers();

        // Assert that only the featured user is returned
        assertEquals(2, rankedUsers.size());
        assertEquals(user1, rankedUsers.get(0));
    }

    @Test
    public void testGetAllFriendsForUserForRanking() throws Exception {
        // Create some sample users
        User user1 = new User();
        user1.setFeatured_in_rankings(true);
        List<String> friendList = new ArrayList<>();
        friendList.add("friend1"); // Assume friend1 is also featured
        user1.setFriends(friendList);
        user1.setId(1L);

        User friend1 = new User();
        friend1.setFeatured_in_rankings(true);

        List<User> userList = new ArrayList<>();
        userList.add(user1);

        // Mock the userRepository to return the sample users
        when(userRepository.findByUsername("user1")).thenReturn(user1);
        when(userRepository.findByUsername("friend1")).thenReturn(friend1);
        when(userService.getUser(1L)).thenReturn(user1);

        // Call the method under test
        List<String> rankedFriends = (userService.getUser(1L)).getFriends();

        // Assert that only the featured friend is returned
        assertEquals(1, rankedFriends.size());
        assertEquals("friend1", rankedFriends.get(0));
    }

    // Add more tests to cover different scenarios and edge cases related to rankings functionality
}
