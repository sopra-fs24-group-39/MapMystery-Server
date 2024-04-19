package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ch.uzh.ifi.hase.constants.lobbyStates;


import static org.junit.jupiter.api.Assertions.*;


public class LobbyServiceTest {
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private LobbyService lobbyService = new LobbyService();

    private Lobby lobby = new Lobby();


    @BeforeEach
    public void setup() {
        user1 = new User(); user1.setId(1L);
        user2 = new User(); user2.setId(2L);
        user3 = new User(); user3.setId(3L);
        user4 = new User(); user4.setId(4L);
        lobby.setPlayerLimit(3);
        lobby.setRounds(5);
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
        lobbyService.addPlayer(user1,lobby);
        lobbyService.addPlayer(user2,lobby);
        lobbyService.addPlayer(user3,lobby);
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
        assertEquals(lobbyStates.FINISHED, lobby.getState());
    }

    @Test
    public void checkNextRound_falseForMismatch() throws Exception {
        lobbyService.addPlayer(user1,lobby);
        lobbyService.addPlayer(user2,lobby);
        lobbyService.advanceRound(user1.getId(),lobby);
        assertFalse(lobbyService.checkNextRound(lobby));
    }

    
}
