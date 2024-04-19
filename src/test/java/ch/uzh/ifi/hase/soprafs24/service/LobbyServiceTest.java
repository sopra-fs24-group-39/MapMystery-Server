package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.GameMode1;
import ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ch.uzh.ifi.hase.constants.lobbyStates;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import ch.uzh.ifi.hase.constants.GameModes;

import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;


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

    @InjectMocks
    private LobbyService lobbyService = new LobbyService();

    private Lobby lobby = new GameMode1();


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user1 = new User(); user1.setId(1L);
        user2 = new User(); user2.setId(2L);
        user3 = new User(); user3.setId(3L);
        user4 = new User(); user4.setId(4L);

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
    public void testStateTransitionToPlaying() throws Exception {
        List<Double> mockCoordinates = Arrays.asList(1.234, 5.678);  // Example coordinates
        when(gameService.get_image_coordinates()).thenReturn(mockCoordinates);
        lobbyService.joinLobby(user1,lobby);
        lobbyService.joinLobby(user2,lobby);
        lobbyService.joinLobby(user3,lobby);
        assertEquals(lobbyStates.PLAYING, lobby.getState());
    }

    @Test
    public void removePlayer_success() throws Exception {
        lobbyService.addPlayer(user1,lobby);
        lobbyService.addPlayer(user2,lobby);
        lobbyService.addPlayer(user3,lobby);
        assertEquals(3, lobby.getPlayers().size());
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
        assertEquals(2, lobby.getCurrRound().get(1L));
    }

    @Test
    public void advanceRound_checkEndGame() throws Exception {
        lobbyService.addPlayer(user1,lobby);
        for (int i = 0; i < 5; i++) {
            lobbyService.advanceRound(user1.getId(),lobby);
        }
        assertEquals(lobbyStates.CLOSED, lobby.getState());
    }

    @Test
    public void checkNextRound_falseForMismatch() throws Exception {
        lobbyService.addPlayer(user1,lobby);
        lobbyService.addPlayer(user2,lobby);
        lobbyService.advanceRound(user1.getId(),lobby);
        assertFalse(lobbyService.checkNextRound(lobby));
    }

    @Test
    public void testJoinLobby_LobbyFull() throws Exception {
      // Simulate a full lobby
      when(lobby.getPlayers().size()).thenReturn(lobby.getPlayerLimit());
      assertThrows(Exception.class, () -> lobbyService.joinLobby(user1, lobby));
    }

    @Test
    public void testCreateLobby_Success() throws Exception {
        Lobby newLobby = lobbyService.createLobby(GameModes.Gamemode1);
        assertNotNull(newLobby);
        assertEquals(GameModes.Gamemode1, newLobby.getGamemode());
    }

    @Test
    public void testPutToSomeLobby_Success() throws Exception {
        when(lobbyRepository.findAll()).thenReturn(Arrays.asList(lobby));
        when(lobby.getState()).thenReturn(lobbyStates.OPEN);
        when(lobby.getGamemode()).thenReturn(GameModes.Gamemode1);

        Long result = lobbyService.putToSomeLobby(user1, GameModes.Gamemode1);
        assertEquals(lobby.getId(), result);
    }

    @Test
    public void testPutToSomeLobby_NoOpenLobbies() throws Exception {
        when(lobbyRepository.findAll()).thenReturn(Arrays.asList());
        when(lobbyService.getLobbyLimit()).thenReturn(1);

        Long result = lobbyService.putToSomeLobby(user1, GameModes.Gamemode1);
        assertEquals(-1L, result);
    }

    @Test
    public void submitScore_Success() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobbyService.submitScore(100, user1.getId(), lobby);
        assertEquals(100, lobby.getDistances().get(user1.getId()).intValue());
    }

    @Test
    public void submitScore_AdvanceRoundAndCheckNextRound() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobbyService.addPlayer(user2, lobby);
        lobbyService.submitScore(100, user1.getId(), lobby);
        lobbyService.submitScore(100, user2.getId(), lobby);
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
        assertEquals(lobbyStates.CLOSED, lobby.getState());
        verify(messagingTemplate).convertAndSend(String.format("/topic/GameMode1/lobby/%s", lobby.getId()), "Game finished");
    }

    @Test
    public void testGetLobby_LobbyExists() {
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(lobby);
        Lobby result = lobbyService.getLobby(1L);
        assertNotNull(result);
        assertEquals(lobby, result);
    }

    @Test
    public void testGetLobby_LobbyDoesNotExist() {
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(null);
        Lobby result = lobbyService.getLobby(1L);
        assertNull(result);
    }

    @Test
    public void testJoinLobby_LobbyClosed() {
        lobby.setState(lobbyStates.CLOSED);
        assertThrows(Exception.class, () -> lobbyService.joinLobby(user1, lobby));
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
        assertEquals(5, lobbyService.getLobbyLimit());
    }

    @Test
    public void testCheckNextRound_AllPlayersSameRound() {
        lobbyService.addPlayer(user1, lobby);
        lobbyService.addPlayer(user2, lobby);
        assertTrue(lobbyService.checkNextRound(lobby));
    }

    @Test
    public void testAdvanceRound_ExceedMaxRounds() throws Exception {
        for (int i = 0; i < 5; i++) { // Assuming max rounds are 5
            lobbyService.advanceRound(user1.getId(), lobby);
        }
        assertThrows(Exception.class, () -> lobbyService.advanceRound(user1.getId(), lobby));
    }

    @Test
    public void testCheckGameState_AllPlayersFinished() {
        when(lobby.getCurrRound()).thenReturn(Map.of(user1.getId(), 5, user2.getId(), 5)); // Assuming max rounds are 5
        assertTrue(lobbyService.checkGameState(lobby));
    }

    @Test
    public void testEndGame_SendsNotificationToAllPlayers() throws Exception {
        lobbyService.addPlayer(user1, lobby);
        lobbyService.addPlayer(user2, lobby);
        lobbyService.endGame(lobby);
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), eq("Game finished"));
    }


    
}
