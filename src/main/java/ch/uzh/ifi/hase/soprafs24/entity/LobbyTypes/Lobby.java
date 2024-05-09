/**
 * author: david 
 */

package ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import java.util.Map;
import java.util.HashMap;
import ch.uzh.ifi.hase.constants.lobbyStates;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.constants.GameModes;
import org.springframework.lang.Nullable;

@Entity
 @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
 @Table(name = "LOBBY")
 public class Lobby implements Serializable {
  
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long lobbyId;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  public List<User> players = new ArrayList<User>();

  @Column
  @Nullable
  private String authKey;

  @Column
  private int playingRound = 0;

  @Column
  private Boolean public_lobby = true;

  @Column
  private Long roundDuration = 1000L*40L;

  @Column
  protected int playerLimit;

  @Column
  protected GameModes gamemode;

  @Column
  protected int rounds;

  @Column    
  private lobbyStates state = lobbyStates.OPEN;
   
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "game_rounds", joinColumns = @JoinColumn(name = "game_id"))
  @MapKeyColumn(name = "player_id")
  @Column(name = "current_round")
  public Map<Long, Integer> currRound = new HashMap<>();  
 
  @ElementCollection( fetch = FetchType.EAGER)
  @CollectionTable(name = "lobby_points", joinColumns = @JoinColumn(name = "lobby_id"))
  @MapKeyColumn(name = "player_id")
  @Column(name = "points")
  protected Map<Long, Float> points = new HashMap<>();
 
  public Long getId(){
    return lobbyId;
  }
 
  public List<User> getPlayers(){
   return players;
  }

  public void setGamemode(GameModes gamemode){
    this.gamemode = gamemode;
  }

  public GameModes getGamemode(){
    return this.gamemode;
  }

  public int getRounds(){
    return rounds;
  }

  public void setPlayingRound(int round){
    this.playingRound = round;
  }

  public int getPlayingRound(){
    return playingRound;
  }

  public void setRounds(int rounds){
    this.rounds = rounds;
  }

  public Map<Long, Float> getPoints(){
   return points;
  }

  public Map<Long,Integer> getCurrRound(){
   return currRound;
  }
   
  public int getPlayerLimit(){
   return playerLimit;
  }

  public void setPlayerLimit(int limit){
    playerLimit = limit;
  }

  public void setRoundDuration(Long duration){
    this.roundDuration = duration;
  }

  public Long getRoundDuration(){
    return roundDuration;
  }

  public void setPrivate(){this.public_lobby = false;}

     public void setPublic(){this.public_lobby = true;}

  public Boolean isPublic(){return this.public_lobby; }

  public lobbyStates getState(){
    return state;
  }   

  public void setLobbyState(lobbyStates lobbystate){
    this.state = lobbystate;
  }

  public void setState(lobbyStates state ){
    this.state = state;
  }

  public Map<Long, Integer> getCurrRounds(){
    return currRound;
  }

  public String getAuthKey(){
      return authKey;
  }

  public void setAuthKey(String authKey){this.authKey = authKey;}
 
  public void setPoints(float points,float timeDelta,Long playerId){
    
  }


}
 