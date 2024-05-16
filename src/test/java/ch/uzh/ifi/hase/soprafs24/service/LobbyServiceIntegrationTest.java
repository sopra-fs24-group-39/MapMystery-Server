package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.GameMode1;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import java.util.concurrent.TimeUnit;

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
import java.util.concurrent.CountDownLatch;

import ch.uzh.ifi.hase.constants.GameModes;

import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.*;


public class LobbyServiceIntegrationTest {
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private Lobby lobby = new GameMode1();

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

    CountDownLatch latch = new CountDownLatch(1);



    @BeforeEach
    public void setup() throws Exception{
        MockitoAnnotations.openMocks(this);

        Long roundDuration = 300L;
        lobby.setRoundDuration(roundDuration);
        user1 = new User(); user1.setId(1L); user1.setUsername("user1");
        user2 = new User(); user2.setId(2L); user2.setUsername("user2");
        user3 = new User(); user3.setId(3L); user3.setUsername("user3");
        user4 = new User(); user4.setId(4L); user4.setUsername("user4");
        // executes schedules tasks after roundDuration+100ms

        when(taskScheduler.schedule(any(Runnable.class), any(Date.class)))
        .thenAnswer(invocation -> {
            Runnable task = invocation.getArgument(0, Runnable.class);
            new Thread(() -> {
                try {
                    Thread.sleep(lobby.getRoundDuration()+150); // Delay execution by 600 milliseconds
                    task.run();
                    latch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            return null;                                                        
        });
    }



    
  

  @Test
  public void completeGameFlow_Ideal_minimalMocking() throws Exception{
    // Overwrite the default mock behavior for this test
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    when(gameService.get_image_coordinates()).thenReturn(Arrays.asList(1.234, 5.678));

    lobby.setRounds(2);   

    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    assertEquals(3, lobby.getPlayers().size());

    assertEquals(lobby.getPlayingRound(),1);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);

    lobbyService.submitScore(2, 0, user1.getId(), lobby);
    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);


    assertEquals(3, lobby.getPlayers().size());
    assertEquals(lobby.getPlayingRound(),2);

    lobbyService.submitScore(2, 0, user1.getId(), lobby);
    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);

    assertEquals(lobby.getState(), lobbyStates.CLOSED);

    Thread.sleep(800);
    boolean completed = latch.await(1, TimeUnit.SECONDS);

    assertTrue(completed, "The scheduled task did not complete in time");
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    verify(messagingTemplate, times(2)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

  }

  @Test
  public void completeGameFlow_OnePlayerLeaves_minimalMocking() throws Exception{
    // Overwrite the default mock behavior for this test
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    when(gameService.get_image_coordinates()).thenReturn(Arrays.asList(1.234, 5.678));

    lobby.setRounds(2);
    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    assertEquals(3, lobby.getPlayers().size());

    assertEquals(lobby.getPlayingRound(),1);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);

    lobbyService.submitScore(2, 0, user1.getId(), lobby);
    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);


    assertEquals(3, lobby.getPlayers().size());
    assertEquals(lobby.getPlayingRound(),2);

    lobbyService.removePlayer(user1, lobby);
    assertEquals(2, lobby.getPlayers().size());

    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);

    assertEquals(lobby.getState(), lobbyStates.CLOSED);
    Thread.sleep(800);
    boolean completed = latch.await(1, TimeUnit.SECONDS);

    assertTrue(completed, "The scheduled task did not complete in time");
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    verify(messagingTemplate, times(2)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

  }


  @Test
  public void completeGameFlow_OnePlayerInactiveLastRound_minimalMocking() throws Exception{
    // Overwrite the default mock behavior for this test
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    when(gameService.get_image_coordinates()).thenReturn(Arrays.asList(1.234, 5.678));

    lobby.setRounds(2);

    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    assertEquals(3, lobby.getPlayers().size());

    assertEquals(lobby.getPlayingRound(),1);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);

    lobbyService.submitScore(2, 0, user1.getId(), lobby);
    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);


    assertEquals(3, lobby.getPlayers().size());
    assertEquals(lobby.getPlayingRound(),2);


    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);

    Thread.sleep(600);


    assertEquals(lobbyStates.CLOSED, lobby.getState());
    boolean completed = latch.await(1, TimeUnit.SECONDS);

    assertTrue(completed, "The scheduled task did not complete in time");
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    verify(messagingTemplate, times(2)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

  }


  @Test
  public void completeGameFlow_OnePlayerInactivefirstRound_minimalMocking() throws Exception{
    // Overwrite the default mock behavior for this test
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    when(gameService.get_image_coordinates()).thenReturn(Arrays.asList(1.234, 5.678));

    lobby.setRounds(2);

    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    assertEquals(3, lobby.getPlayers().size());

    assertEquals(lobby.getPlayingRound(),1);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);

    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);
    //waiting for inactive players to get kcicked out
    Thread.sleep(600);
    assertEquals(lobby.getState(), lobbyStates.PLAYING);


    assertEquals(2, lobby.getPlayers().size());
    assertEquals(lobby.getPlayingRound(),2);

    lobbyService.submitScore(2, 0, user1.getId(), lobby);
    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);

    assertEquals(lobbyStates.CLOSED, lobby.getState());

    Thread.sleep(600);
    boolean completed = latch.await(1, TimeUnit.SECONDS);

    assertTrue(completed, "The scheduled task did not complete in time");
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    verify(messagingTemplate, times(2)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

  }


  @Test
  public void completeGameFlow_OnePlayerInactiveSecondRound_minimalMocking() throws Exception{
    // Overwrite the default mock behavior for this test
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    when(gameService.get_image_coordinates()).thenReturn(Arrays.asList(1.234, 5.678));

    lobby.setRounds(3);

    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    assertEquals(3, lobby.getPlayers().size());

    assertEquals(lobby.getPlayingRound(),1);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    
    lobbyService.submitScore(2, 0, user1.getId(), lobby);
    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);
    assertEquals(lobby.getState(), lobbyStates.PLAYING);


    assertEquals(3, lobby.getPlayers().size());
    assertEquals(lobby.getPlayingRound(),2);

    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);
    //waiting for inactive players to get kcicked out
    Thread.sleep(600);
    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    assertEquals(lobby.getPlayingRound(),3);
    assertEquals(2, lobby.getPlayers().size());


    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);


    assertEquals(lobbyStates.CLOSED, lobby.getState());
    //waiting for the last leaderboards to be sent
    Thread.sleep(600);

    boolean completed = latch.await(1, TimeUnit.SECONDS);

    assertTrue(completed, "The scheduled task did not complete in time");
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
    verify(messagingTemplate, times(4)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

  }

  @Test
  public void completeGameFlow_OnePlayerInactiveSecondRoundAndOnePlayerLeaves_minimalMocking() throws Exception{
    // Overwrite the default mock behavior for this test
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    when(gameService.get_image_coordinates()).thenReturn(Arrays.asList(1.234, 5.678));

    lobby.setRounds(3);

    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    assertEquals(3, lobby.getPlayers().size());

    assertEquals(lobby.getPlayingRound(),1);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    
    lobbyService.submitScore(2, 0, user1.getId(), lobby);
    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);
    assertEquals(lobby.getState(), lobbyStates.PLAYING);


    assertEquals(3, lobby.getPlayers().size());
    assertEquals(lobby.getPlayingRound(),2);

    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);
    //waiting for inactive players to get kcicked out
    lobbyService.removePlayer(user2, lobby);
    Thread.sleep(600);
    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    assertEquals(lobby.getPlayingRound(),3);
    assertEquals(1, lobby.getPlayers().size());


    lobbyService.submitScore(2, 0, user3.getId(), lobby);


    assertEquals(lobbyStates.CLOSED, lobby.getState());
    //waiting for the last leaderboards to be sent
    Thread.sleep(600);

    boolean completed = latch.await(1, TimeUnit.SECONDS);

    assertTrue(completed, "The scheduled task did not complete in time");
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
    verify(messagingTemplate, times(4)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

  }


  @Test
  public void completeGameFlow_TwoInactivePlayers_minimalMocking() throws Exception{
    // Overwrite the default mock behavior for this test
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    when(gameService.get_image_coordinates()).thenReturn(Arrays.asList(1.234, 5.678));

    lobby.setRounds(3);

    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    assertEquals(3, lobby.getPlayers().size());

    assertEquals(lobby.getPlayingRound(),1);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    
    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);
    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    Thread.sleep(600);


    assertEquals(2, lobby.getPlayers().size());
    assertEquals(lobby.getPlayingRound(),2);

    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    //waiting for inactive players to get kcicked out
    Thread.sleep(600);
    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    assertEquals(lobby.getPlayingRound(),3);
    assertEquals(1, lobby.getPlayers().size());


    lobbyService.submitScore(2, 0, user2.getId(), lobby);


    assertEquals(lobbyStates.CLOSED, lobby.getState());
    //waiting for the last leaderboards to be sent
    Thread.sleep(600);

    boolean completed = latch.await(1, TimeUnit.SECONDS);

    assertTrue(completed, "The scheduled task did not complete in time");
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
    verify(messagingTemplate, times(4)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

  }

  @Test
  public void completeGameFlow_AllInactivePlayers_minimalMocking() throws Exception{
    // Overwrite the default mock behavior for this test
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    when(gameService.get_image_coordinates()).thenReturn(Arrays.asList(1.234, 5.678));

    lobby.setRounds(3);

    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    //waiting for the last leaderboards to be sent
    assertEquals(3, lobby.getPlayers().size());
    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    assertEquals(lobby.getPlayingRound(),1);


    Thread.sleep(600);
    assertEquals(0, lobby.getPlayers().size());
    assertEquals(lobbyStates.CLOSED, lobby.getState());


    boolean completed = latch.await(1, TimeUnit.SECONDS);

    assertTrue(completed, "The scheduled task did not complete in time");
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
    verify(messagingTemplate, times(2)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    verify(messagingTemplate, times(1)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

  }

  @Test
  public void completeGameFlow_AllInactivePlayersLastRound_minimalMocking() throws Exception{
    // Overwrite the default mock behavior for this test
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    when(gameService.get_image_coordinates()).thenReturn(Arrays.asList(1.234, 5.678));

    lobby.setRounds(2);

    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    assertEquals(3, lobby.getPlayers().size());
    assertEquals(lobby.getPlayingRound(),1);
    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    
    lobbyService.submitScore(2, 0, user1.getId(), lobby);
    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);
    
    
    //waiting for the last leaderboards to be sent
    assertEquals(3, lobby.getPlayers().size());
    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    assertEquals(lobby.getPlayingRound(),2);


    Thread.sleep(600);
    assertEquals(0, lobby.getPlayers().size());
    assertEquals(lobbyStates.CLOSED, lobby.getState());

    boolean completed = latch.await(1, TimeUnit.SECONDS);

    assertTrue(completed, "The scheduled task did not complete in time");
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    verify(messagingTemplate, times(2)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

  }

  @Test
  public void completeGameFlow_AllPlayersLeaveImmediate_minimalMocking() throws Exception{
    // Overwrite the default mock behavior for this test
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    when(gameService.get_image_coordinates()).thenReturn(Arrays.asList(1.234, 5.678));

    lobby.setRounds(3);

    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    assertEquals(3, lobby.getPlayers().size());

    assertEquals(lobby.getPlayingRound(),1);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);
    
    lobbyService.removePlayer(user1, lobby);
    lobbyService.removePlayer(user2, lobby);
    lobbyService.removePlayer(user3, lobby);
    
    //waiting for the last leaderboards to be sent
    Thread.sleep(600);
    assertEquals(0, lobby.getPlayers().size());
    assertEquals(lobby.getState(), lobbyStates.CLOSED);


    boolean completed = latch.await(1, TimeUnit.SECONDS);

    assertTrue(completed, "The scheduled task did not complete in time");
    verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
    verify(messagingTemplate, times(2)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    verify(messagingTemplate, times(1)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

  }
}
