/*
package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.constants.GameModes;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "game_log")
public class GameLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @OneToMany
    private List<User> players;

    @Column(nullable = false)
    private int rounds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameModes gameMode;

    // New field for scores
    @ElementCollection
    @CollectionTable(name = "game_scores", joinColumns = @JoinColumn(name = "game_log_id"))
    @MapKeyJoinColumn(name = "user_id")
    @Column(name = "score")
    private Map<User, Integer> scores;

    public GameLog(List<Integer> scores) {
        this.scores = (Map<User, Integer>) scores;
    }

    public GameLog() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public GameModes getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameModes gameMode) {
        this.gameMode = gameMode;
    }
}
*/
