package ch.uzh.ifi.hase.soprafs24.Entities;

import ch.uzh.ifi.hase.constants.GameModes;
import ch.uzh.ifi.hase.soprafs24.entity.GameLog;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GameLogTests {

    private GameLog gameLog;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        gameLog = new GameLog();
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
    }

    @Test
    public void testGettersAndSetters() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        List<User> players = List.of(user1, user2);
        int rounds = 5;
        GameModes gameMode = GameModes.Gamemode1;

        Map<User, Integer> scores = new HashMap<>();
        scores.put(user1, 100);
        scores.put(user2, 150);

        gameLog.setStartTime(startTime);
        gameLog.setEndTime(endTime);
        gameLog.setPlayers(players);
        gameLog.setRounds(rounds);
        gameLog.setGameMode(gameMode);

        assertThat(gameLog.getStartTime()).isEqualTo(startTime);
        assertThat(gameLog.getEndTime()).isEqualTo(endTime);
        assertThat(gameLog.getPlayers()).isEqualTo(players);
        assertThat(gameLog.getRounds()).isEqualTo(rounds);
        assertThat(gameLog.getGameMode()).isEqualTo(gameMode);
    }


}