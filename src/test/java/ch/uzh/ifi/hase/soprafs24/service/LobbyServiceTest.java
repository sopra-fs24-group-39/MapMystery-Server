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


public class LobbyServiceTest {
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
    public void testConstructor_initialState() {
        assertEquals(lobbyStates.OPEN, lobby.getState());
        assertTrue(lobby.getPlayers().isEmpty());
    }

    @Test
    public void addPlayer_success() throws Exception {
      lobbyService.addPlayer(user1,lobby);
      assertEquals(1, lobby.getPlayers().size());
    }

    @Test
    public void addPlayer_success_return_CorrectId() throws Exception {
      lobbyService.addPlayer(user1,lobby);
      Long LobbyId = lobbyService.addPlayer(user1, lobby);
      assertEquals(2, lobby.getPlayers().size());
      assertEquals(LobbyId, lobby.getId());
    }


    @Test
    public void testStateTransitionToPlaying() throws Exception {
        when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);
        List<Double> mockCoordinates = Arrays.asList(1.234, 5.678);  // Example coordinates
        when(gameService.get_image_coordinates()).thenReturn(mockCoordinates);
        lobbyService.joinLobby(user1,lobby,null);
        lobbyService.joinLobby(user2,lobby,null);
        lobbyService.joinLobby(user3,lobby,null);
        assertEquals(lobbyStates.PLAYING, lobby.getState());
    }

    @Test
    public void removePlayer_success() throws Exception {
        lobbyService.addPlayer(user1,lobby);
        lobbyService.addPlayer(user2,lobby);
        lobbyService.addPlayer(user3,lobby);
        lobbyService.removePlayer(user1, lobby);
        assertEquals(2, lobby.getPlayers().size());
    }

    @Test
    public void removePlayer_success_state_unchanged() throws Exception {
        when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);

        List<Double> mockCoordinates = Arrays.asList(1.234, 5.678);  // Example coordinates
        when(gameService.get_image_coordinates()).thenReturn(mockCoordinates);
        lobbyService.joinLobby(user1,lobby,null);
        lobbyService.joinLobby(user2,lobby,null);
        lobbyService.joinLobby(user3,lobby,null);
        lobbyService.removePlayer(user1, lobby);
        assertEquals(lobbyStates.PLAYING, lobby.getState());

    }


    @Test
    public void removePlayer_Endgame() throws Exception {
        when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);

        List<Double> mockCoordinates = Arrays.asList(1.234, 5.678);  // Example coordinates
        when(gameService.get_image_coordinates()).thenReturn(mockCoordinates);
        lobbyService.joinLobby(user1,lobby,null);
        lobbyService.removePlayer(user1, lobby);
        assertEquals(lobbyStates.CLOSED, lobby.getState());

    }

    @Test
    public void joinLobbyMessagesSend() throws Exception {
        List<Double> mockCoordinates = Arrays.asList(1.234, 5.678);  // Example coordinates
        when(gameService.get_image_coordinates()).thenReturn(mockCoordinates);
        when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
        lobbyService.joinLobby(user1,lobby,null);
        lobbyService.joinLobby(user2,lobby,null);
        lobbyService.joinLobby(user3,lobby,null);
        verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
        verify(messagingTemplate, times(1)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/coordinates/%s", lobby.getId())), anyMap());

    }

    @Test
    public void removePlayer_failure() throws Exception {
        lobbyService.addPlayer(user1,lobby);
        assertThrows(Exception.class, () -> lobbyService.removePlayer(user3,lobby));
    }

    @Test
    public void advanceRound_success() throws Exception {
        lobbyService.addPlayer(user1,lobby);
        lobbyService.advanceRound(user1.getId(),lobby);
        assertEquals(1, lobby.getCurrRound().get(1L));
    }

    @Test
    public void advanceRound_checkEndGame() throws Exception {
        lobby.setRounds(5);
        lobbyService.addPlayer(user1,lobby);
        for (int i = 0; i < lobby.getRounds(); i++) {
            lobbyService.advanceRound(user1.getId(),lobby);
        }
        assertEquals(lobbyStates.CLOSED, lobby.getState());
    }

    @Test
    public void checkNextRound_falseForMismatch() throws Exception {
        when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);
        lobbyService.addPlayer(user1,lobby);
        lobbyService.addPlayer(user2,lobby);
        lobbyService.advanceRound(user1.getId(),lobby);
        lobby.setPlayingRound(1);
        assertFalse(lobbyService.checkNextRound(lobby));
    }


    @Test
    public void testCreateLobby_Success() throws Exception {
        Lobby newLobby = lobbyService.createLobby(GameModes.Gamemode1);
        assertNotNull(newLobby);
        assertEquals(GameModes.Gamemode1, newLobby.getGamemode());
    }

    @Test
    public void testCreatePrivateLobby_Sucess() throws Exception {
        Lobby newLobby = lobbyService.createPrivateLobby(GameModes.Gamemode1);
        assertNotNull(newLobby);
        assertEquals(GameModes.Gamemode1, newLobby.getGamemode());
        assertNotNull(newLobby.getAuthKey());
    }

    @Test
    public void testJoinPrivateLobby_Sucess() throws Exception {
        Lobby newLobby = lobbyService.createPrivateLobby(GameModes.Gamemode1);
        String authKey = newLobby.getAuthKey();
        assertNotNull(newLobby.getAuthKey());
        lobbyService.joinLobby(user1,newLobby,authKey);
    }

    @Test
    public void testJoinPrivateLobby_Fail_IncorrectKey() throws Exception {
        Lobby newLoby = lobbyService.createPrivateLobby(GameModes.Gamemode1);
        String authKey = "Hello World";
        assertThrows(Exception.class, () -> lobbyService.joinLobby(user1,newLoby,authKey));
    }

    @Test
    public void testJoinPrivateLobby_Fail_NoKey() throws Exception {
        Lobby newLoby = lobbyService.createPrivateLobby(GameModes.Gamemode1);
        assertThrows(Exception.class, () -> lobbyService.joinLobby(user1,newLoby,null));
    }


    @Test
    public void testPutToSomeLobby_Success() throws Exception {
        when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));

        Long result = lobbyService.putToSomeLobby(user1, GameModes.Gamemode1);
        assertEquals(lobby.getId(), result);
    }

    @Test
    public void testPutToSomeLobby_NoOpenLobbies() throws Exception {
        when(lobbyRepository.findAll()).thenReturn(Arrays.asList());

        Long result = lobbyService.putToSomeLobby(user1, GameModes.Gamemode1);
        assertNotEquals(-1L, result);
    }



    @Test
    public void submitScore_AdvanceRoundAndCheckNextRound() throws Exception {
        when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);
        lobby.setPlayingRound(1);
        lobbyService.addPlayer(user1, lobby);
        lobbyService.addPlayer(user2, lobby);
        lobbyService.submitScore(100,0, user1.getId(), lobby);
        lobbyService.submitScore(100,0, user2.getId(), lobby);
        assertTrue(lobbyService.checkNextRound(lobby));
    }

    @Test
    public void testRefreshLobbies_RemoveClosedLobby() throws Exception {
        Lobby closedLobby = new GameMode1();
        closedLobby.setState(lobbyStates.CLOSED);
        when(lobbyRepository.findAll()).thenReturn(Arrays.asList(closedLobby));

        lobbyService.refreshLobbies();
        verify(lobbyRepository).delete(closedLobby);
    }

    @Test
    public void testGetAllLobbies_ReturnsNonEmptyList() {
        when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
        List<Lobby> result = lobbyService.getAllLobbies();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void testEndGame_SetsStateToClosedAndNotifiesPlayers() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobbyService.endGame(lobby);
        Map<String,Float> expected = new HashMap<>();
        expected.put(null, 0.0f);
        assertEquals(lobbyStates.CLOSED, lobby.getState());
        verify(messagingTemplate,times(1)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());
    }

    @Test
    public void testGetLobby_LobbyExists() throws Exception {
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(lobby);
        Lobby result = lobbyService.getLobby(1L);
        assertNotNull(result);
        assertEquals(lobby, result);
    }

    @Test
    public void testGetLobby_LobbyDoesNotExist() throws Exception {
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(null);
        Lobby result = lobbyService.getLobby(1L);
        assertNull(result);
    }

    @Test
    public void testPutToSomeLobbyAllClosed() throws Exception{
        when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
        lobby.setState(lobbyStates.CLOSED);
        lobbyService.setLobbyLimit(1);
        assertThrows(Exception.class,()-> lobbyService.putToSomeLobby(user1, GameModes.Gamemode1));
    }

    @Test
    public void testRemovePlayer_NotInLobby() {
        assertThrows(Exception.class, () -> lobbyService.removePlayer(user1, lobby));
    }

    @Test
    public void testGetLobbyCount() {
        when(lobbyRepository.count()).thenReturn(5L);
        Long count = lobbyService.getLobbyCount();
        assertEquals(5L, count.longValue());
    }

    @Test
    public void testGetLobbyLimit() {
        assertEquals(10, lobbyService.getLobbyLimit());
    }

    @Test
    public void testCheckNextRound_AllPlayersSameRound()throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobbyService.addPlayer(user2, lobby);
        assertTrue(lobbyService.checkNextRound(lobby));
    }

    @Test
    public void testAdvanceRound_ExceedMaxRounds() throws Exception {
        lobbyService.joinLobby(user1, lobby,null);
        for (int i = 0; i < 5; i++) { // Assuming max rounds are 5
            lobbyService.advanceRound(user1.getId(), lobby);
        }
        assertEquals(lobby.getCurrRound().get(user1.getId()),5);
    }

    @Test
    public void testEndGame_SendsNotificationToAllPlayers() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobbyService.addPlayer(user2, lobby);
        lobbyService.endGame(lobby);
        verify(messagingTemplate, times(2)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
        verify(messagingTemplate).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());

    }


    @Test 
    public void testCompleteGameFlow() throws Exception{
      when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);
      when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
      when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(lobby);
      when(lobbyRepository.findByLobbyId(null)).thenReturn(lobby);


      lobby.setPublic();
      lobby.setPlayerLimit(3);
      lobby.setRounds(3);
      assertEquals(lobby.getState(), lobbyStates.OPEN);
      lobbyService.putToSomeLobby(user1, GameModes.Gamemode1);
      lobbyService.putToSomeLobby(user2, GameModes.Gamemode1);
      lobbyService.putToSomeLobby(user3, GameModes.Gamemode1);

      assertEquals(lobbyStates.PLAYING,lobby.getState());
      List<User> expectedPlayers = new ArrayList<User>();
      expectedPlayers.add(user1);
      expectedPlayers.add(user2);
      expectedPlayers.add(user3);
     

      assertEquals(expectedPlayers, lobby.getPlayers());

      lobbyService.submitScore(200,0, user1.getId(), lobby);
      lobbyService.submitScore(200,0, user2.getId(), lobby);
      lobbyService.submitScore(200,0, user3.getId(), lobby);

      assertEquals(lobby.getCurrRound().get(user1.getId()), 1);
      assertEquals(lobby.getCurrRound().get(user2.getId()), 1);
      assertEquals(lobby.getCurrRound().get(user3.getId()), 1);

      lobbyService.submitScore(200,0, user1.getId(), lobby);
      lobbyService.submitScore(200,0, user2.getId(), lobby);
      lobbyService.submitScore(200,0, user3.getId(), lobby);

      assertEquals(lobby.getCurrRound().get(user1.getId()), 2);
      assertEquals(lobby.getCurrRound().get(user2.getId()), 2);
      assertEquals(lobby.getCurrRound().get(user3.getId()), 2);

      lobbyService.submitScore(200,0, user1.getId(), lobby);
      lobbyService.submitScore(200,0, user2.getId(), lobby);
      lobbyService.submitScore(200,0, user3.getId(), lobby);

      assertEquals(lobby.getCurrRound().get(user1.getId()), 3);
      assertEquals(lobby.getCurrRound().get(user2.getId()), 3);
      assertEquals(lobby.getCurrRound().get(user3.getId()), 3);

      

      assertEquals(lobbyStates.CLOSED,lobby.getState());
    }


    @Test
    public void kickOutInactivePlayers_AllPlayersActive() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
      lobby.setRoundDuration(400L); // 1 second for quick test
      lobby.setPlayingRound(1);
      lobbyService.addPlayer(user1, lobby);
      lobbyService.addPlayer(user2, lobby);
      lobby.currRound.put(user1.getId(), 1);
      lobby.currRound.put(user2.getId(), 1);
      
      lobbyService.createKickOutInactivePlayers(lobby,1); // Current round is 1
      Thread.sleep(500L); // Wait a bit longer than the round duration to ensure the task executes
      
      assertEquals(2, lobby.getPlayers().size());
      assertTrue(lobby.getPlayers().containsAll(Arrays.asList(user1, user2)));
  }

  @Test
  public void kickOutInactivePlayers_OnePlayerInactive() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
      lobby.setRoundDuration(400L); // 1 second for quick test
      lobbyService.addPlayer(user1, lobby);
      lobbyService.addPlayer(user2, lobby);
      lobby.setPlayingRound(1);
      lobby.currRound.put(user1.getId(), 1);
      lobby.currRound.put(user2.getId(), 0); // user2 is inactive
      
      lobbyService.createKickOutInactivePlayers(lobby,1); // Current round is 1
      Thread.sleep(500L); // Wait a bit longer than the round duration to ensure the task executes
      
      assertEquals(1, lobby.getPlayers().size());
      assertTrue(lobby.getPlayers().contains(user1));
      assertFalse(lobby.getPlayers().contains(user2));
  }

  @Test
  public void kickOutInactivePlayers_NoPlayersInactive() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
      lobby.setRoundDuration(400L); // 1 second for quick test
      lobbyService.addPlayer(user1, lobby);
      lobbyService.addPlayer(user2, lobby);
      
      lobbyService.createKickOutInactivePlayers(lobby,0); // Checking before any round starts
      Thread.sleep(500L); // Wait a bit longer than the round duration to ensure the task executes
      
      assertEquals(2, lobby.getPlayers().size());
  }

  @Test
  public void kickOutInactivePlayers_AllPlayersInactive() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);

      lobby.setRoundDuration(400L); // 1 second for quick test
      lobbyService.addPlayer(user1, lobby);
      lobbyService.addPlayer(user2, lobby);
      lobby.setPlayingRound(1);

      
      lobbyService.createKickOutInactivePlayers(lobby,1); // Current round is 1, but no players have moved
      Thread.sleep(500L); // Wait a bit longer than the round duration to ensure the task executes
      
      assertTrue(lobby.getPlayers().isEmpty());
  }


  @Test
  public void completeGameFlow_AllPlayersLeaveDuringGame() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
      lobby.setRoundDuration(400L); // Set a short duration for testing
      lobbyService.addPlayer(user1, lobby);
      lobbyService.addPlayer(user2, lobby);
      lobbyService.addPlayer(user3, lobby);
  
      lobby.currRound.put(user1.getId(), 1);
      lobby.currRound.put(user2.getId(), 0); // user2 does not progress
      lobby.currRound.put(user3.getId(), 1);
      lobby.setPlayingRound(1);

  
      lobbyService.createKickOutInactivePlayers(lobby,1);
      Thread.sleep(1200); // Wait to ensure the scheduled task executes
  
      assertEquals(2, lobby.getPlayers().size());
      assertFalse(lobby.getPlayers().contains(user2));
  
      // Now user3 leaves
      lobby.currRound.remove(user3.getId());
      lobbyService.createKickOutInactivePlayers(lobby,1);
      Thread.sleep(500L); // Allow for task execution
  
      assertEquals(1, lobby.getPlayers().size());
      assertFalse(lobby.getPlayers().contains(user3));
  }

  @Test
  public void waitingForAllPlayersToSubmitGuess() throws Exception {
    when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
    when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);
    when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
    lobby.setRounds(2);
    lobby.setRoundDuration(400L); // Set a short duration for testing
    assertEquals(lobby.getState(), lobbyStates.OPEN);

    lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
    lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

    assertEquals(3, lobby.getPlayers().size());

    assertEquals(lobby.getPlayingRound(),1);

    assertEquals(lobby.getState(), lobbyStates.PLAYING);

    lobbyService.submitScore(2, 0, user2.getId(), lobby);
    lobbyService.submitScore(2, 0, user3.getId(), lobby);

    assertEquals(3, lobby.getPlayers().size());
    assertEquals(1, lobby.getPlayingRound());
  }
  @Test
  public void completeGameFlow_ImmediateAllPlayerExit() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby); 
      lobby.setRoundDuration(400L); // Set a short duration for testing
      lobbyService.addPlayer(user1, lobby);
      lobbyService.addPlayer(user2, lobby);
      lobby.setPlayingRound(1);

      // All players are inactive from the start
      lobbyService.createKickOutInactivePlayers(lobby,1);
      Thread.sleep(500L); // Wait to ensure the scheduled task executes
      assertTrue(lobby.getPlayers().isEmpty());
  }

  @Test
  public void completeGameFlowIdealBehaviour() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
      when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);
      when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
      lobby.setRounds(2);
      lobby.setRoundDuration(400L); // Set a short duration for testing
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
      verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());
      verify(messagingTemplate, times(1)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyMap());




  }

  @Test
  public void completeGameFlowPlayerLeavesFirstRound() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
      when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);
      when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
      lobby.setRounds(2);
      lobby.setRoundDuration(400L); // Set a short duration for testing
      assertEquals(lobby.getState(), lobbyStates.OPEN);

      lobbyService.putToSomeLobby(user1,GameModes.Gamemode1);
      lobbyService.putToSomeLobby(user2,GameModes.Gamemode1);
      lobbyService.putToSomeLobby(user3,GameModes.Gamemode1);

      assertEquals(3, lobby.getPlayers().size());

      assertEquals(lobby.getPlayingRound(),1);

      assertEquals(lobby.getState(), lobbyStates.PLAYING);

      lobbyService.removePlayer(user1, lobby);


      lobbyService.submitScore(2, 0, user2.getId(), lobby);
      lobbyService.submitScore(2, 0, user3.getId(), lobby);

      assertEquals(lobby.getState(), lobbyStates.PLAYING);



      assertEquals(2, lobby.getPlayers().size());
      assertEquals(lobby.getPlayingRound(),2);

      lobbyService.submitScore(2, 0, user2.getId(), lobby);
      lobbyService.submitScore(2, 0, user3.getId(), lobby);

      assertEquals(lobby.getState(), lobbyStates.CLOSED);
      verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());



  }


  @Test
  public void completeGameFlowPlayerLeavesLastRound() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
      when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);
      when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
      lobby.setRounds(2);
      lobby.setRoundDuration(400L); // Set a short duration for testing
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

      lobbyService.removePlayer(user1, lobby);


      assertEquals(2, lobby.getPlayers().size());
      assertEquals(lobby.getPlayingRound(),2);

      lobbyService.submitScore(2, 0, user2.getId(), lobby);
      lobbyService.submitScore(2, 0, user3.getId(), lobby);

      assertEquals(lobby.getState(), lobbyStates.CLOSED);
      verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());



  }

  @Test
  public void completeGameFlowPlayerInactiveFirstRound() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
      when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);
      when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
      lobby.setRounds(2);
      lobby.setRoundDuration(400L); // Set a short duration for testing
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
      lobbyService.kickOutInactivePlayers(1L, 1);


      assertEquals(2, lobby.getPlayers().size());
      assertEquals(lobby.getPlayingRound(),2);

      lobbyService.submitScore(2, 0, user2.getId(), lobby);
      lobbyService.submitScore(2, 0, user3.getId(), lobby);

      assertEquals(lobby.getState(), lobbyStates.CLOSED);
      verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());



  }

  @Test
  public void completeGameFlowPlayerInactiveLastRound() throws Exception {
      when(lobbyRepository.findByLobbyId(any())).thenReturn(lobby);
      when(taskScheduler.schedule(any(Runnable.class), any(Date.class))).thenReturn(null);
      when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
      lobby.setRounds(2);
      lobby.setRoundDuration(400L); // Set a short duration for testing
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
      lobbyService.kickOutInactivePlayers(1L, 1);


      assertEquals(3, lobby.getPlayers().size());
      assertEquals(lobby.getPlayingRound(),2);

      lobbyService.submitScore(2, 0, user2.getId(), lobby);
      lobbyService.submitScore(2, 0, user3.getId(), lobby);

      lobbyService.kickOutInactivePlayers(1L, 2);

      assertEquals(2, lobby.getPlayers().size());
      assertEquals(lobby.getPlayingRound(),2);
      assertEquals(lobby.getState(), lobbyStates.CLOSED);
      verify(messagingTemplate, times(3)).convertAndSend(eq(String.format("/topic/lobby/GameMode1/LeaderBoard/%s", lobby.getId())), anyString());



  }

  
  
  
}
