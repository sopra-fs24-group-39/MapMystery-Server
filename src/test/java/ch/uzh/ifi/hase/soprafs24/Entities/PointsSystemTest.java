package ch.uzh.ifi.hase.soprafs24.Entities;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.GameMode1;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import java.util.Date;

import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.UtilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;

import ch.uzh.ifi.hase.constants.lobbyStates;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ch.uzh.ifi.hase.constants.GameModes;

import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;


import static org.junit.jupiter.api.Assertions.*;

public class PointsSystemTest {
    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private GameService gameService;

    @Mock
    UserRepository userRepository;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private UtilityService utilityService;


    @InjectMocks
    private LobbyService lobbyService = new LobbyService();

    private Lobby lobby = new GameMode1();


    @BeforeEach
    public void setup() throws Exception{
        MockitoAnnotations.openMocks(this);

        user1 = new User(); user1.setId(1L); user1.setUsername("user1");
        user2 = new User(); user2.setId(2L); user2.setUsername("user2");
        user3 = new User(); user3.setId(3L); user3.setUsername("user3");
        user4 = new User(); user4.setId(4L); user4.setUsername("user4");

        when(taskScheduler.schedule(any(Runnable.class), any(Date.class)))
                .thenAnswer(invocation -> {
                    Runnable task = invocation.getArgument(0, Runnable.class);
                    task.run();  // Execute the task immediately
                    return null;  // Since it's a void method, return null
                });


    }

    @Test
    public void test_time_delta_0_distance_0() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobby.setLobbyState(lobbyStates.PLAYING);
        lobbyService.submitScore(0,0, user1.getId(), lobby);
        assertEquals(1000, lobby.getPoints().get(user1.getId()).intValue());
    }

    @Test
    public void test_time_delta_10_distance_2000000() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobby.setLobbyState(lobbyStates.PLAYING);
        lobbyService.submitScore(2000000,10, user1.getId(), lobby);
        assertEquals(831, lobby.getPoints().get(user1.getId()).intValue());
    }
    @Test
    public void test_time_delta_10_distance_1700000() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobby.setLobbyState(lobbyStates.PLAYING);
        lobbyService.submitScore(1700000,10, user1.getId(), lobby);
        assertTrue(845 <= lobby.getPoints().get(user1.getId()).intValue());
    }
    @Test
    public void test_time_delta_30_distance_1000000() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobby.setLobbyState(lobbyStates.PLAYING);
        lobbyService.submitScore(1000000,30, user1.getId(), lobby);
        assertEquals(731, lobby.getPoints().get(user1.getId()).intValue());
    }
    @Test
    public void test_time_delta_70_distance_10000000() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobby.setLobbyState(lobbyStates.PLAYING);
        lobbyService.submitScore(10000000,70, user1.getId(), lobby);
        assertEquals(232, lobby.getPoints().get(user1.getId()).intValue());
    }

    @Test
    public void test_time_delta_50_distance_20000000() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobby.setLobbyState(lobbyStates.PLAYING);
        lobbyService.submitScore(20000000,50, user1.getId(), lobby);
        assertEquals(2, lobby.getPoints().get(user1.getId()).intValue());
    }

    @Test
    public void test_time_delta_100_distance_2000000() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobby.setLobbyState(lobbyStates.PLAYING);
        lobbyService.submitScore(2000000,100, user1.getId(), lobby);
        assertEquals(208, lobby.getPoints().get(user1.getId()).intValue());
    }

    @Test
    public void test_out_of_bound_distance() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobby.setLobbyState(lobbyStates.PLAYING);
        assertThrows(ResponseStatusException.class,()-> {lobbyService.submitScore(21000000,100, user1.getId(), lobby);});
    }

    @Test
    public void test_out_of_bound_Timedelta() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobby.setLobbyState(lobbyStates.PLAYING);
        assertThrows(ResponseStatusException.class,()-> {lobbyService.submitScore(210000,135, user1.getId(), lobby);});
    }




}
