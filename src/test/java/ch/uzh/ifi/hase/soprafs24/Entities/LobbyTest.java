package ch.uzh.ifi.hase.soprafs24.Entities;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class LobbyTest {
    private Lobby lobby;
    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    public void setup() {
        lobby = new Lobby();
        user1 = new User(); user1.setId(1L);
        user2 = new User(); user2.setId(2L);
        user3 = new User(); user3.setId(3L);
        user4 = new User(); user4.setId(4L);
    }

    @Test
    public void testConstructor_initialState() {
        assertEquals("open", lobby.getState());
        assertTrue(lobby.getPlayers().isEmpty());
    }

    @Test
    public void addPlayer_success() throws Exception {
        lobby.addPlayer(user1);
        assertEquals(1, lobby.getPlayers().size());
    }


    @Test
    public void testStateTransitionToPlaying() throws Exception {
        lobby.addPlayer(user1);
        lobby.addPlayer(user2);
        lobby.addPlayer(user3);
        assertEquals("playing", lobby.getState());
    }

    @Test
    public void removePlayer_success() throws Exception {
        lobby.addPlayer(user1);
        lobby.addPlayer(user2);
        lobby.removePlayer(user1);
        assertEquals(1, lobby.getPlayers().size());
    }

    @Test
    public void removePlayer_failure() throws Exception {
        lobby.addPlayer(user1);
        assertThrows(Exception.class, () -> lobby.removePlayer(user3));
    }

    @Test
    public void setScore_checkScore() {
        lobby.setScore(50, 1L);
        assertEquals(50, lobby.getPoints().get(1L));
    }

    @Test
    public void advanceRound_success() throws Exception {
        lobby.addPlayer(user1);
        lobby.advanceRound(1L);
        assertEquals(2, lobby.getCurrRound().get(1L));
    }

    @Test
    public void advanceRound_checkEndGame() throws Exception {
        lobby.addPlayer(user1);
        for (int i = 0; i < 5; i++) {
            lobby.advanceRound(1L);
        }
        assertEquals("finished", lobby.getState());
    }

    @Test
    public void checkNextRound_falseForMismatch() throws Exception {
        lobby.addPlayer(user1);
        lobby.addPlayer(user2);
        lobby.advanceRound(1L);
        assertFalse(lobby.checkNextRound());
    }

    @Test
    public void endGame_changesStateToFinished() {
        lobby.endGame();
        assertEquals("finished", lobby.getState());
    }
}
