/**
 * author: david 
 */

package ch.uzh.ifi.hase.soprafs24.entity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import java.util.Map;
import java.util.HashMap;
import ch.uzh.ifi.hase.constants.lobbyStates;
 
 @Entity
 @Table(name = "LOBBY")
 public class Lobby implements Serializable {
  
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long lobbyId;

  //  TODO: the repository throws an error as soon as the players list grows to 2
  //  @OneToMany(cascade = CascadeType.ALL)   
  @Column
  public ArrayList<User> players = new ArrayList<>();

  @Column
  private int playerLimit;

  @Column
  private int rounds;

  @Column
  private lobbyStates state = lobbyStates.OPEN;
   
  @ElementCollection
  @CollectionTable(name = "game_rounds", joinColumns = @JoinColumn(name = "game_id"))
  @MapKeyColumn(name = "player_id")
  @Column(name = "current_round")
  public Map<Long, Integer> currRound = new HashMap<>();  
 
  @ElementCollection
  @CollectionTable(name = "lobby_points", joinColumns = @JoinColumn(name = "lobby_id"))
  @MapKeyColumn(name = "player_id")
  @Column(name = "points")
  public Map<Long, Integer> distances = new HashMap<>();
 
  public Long getId(){
    return lobbyId;
  }
 
  public List<User> getPlayers(){
   return players;
  }

  public int getRounds(){
    return rounds;
  }

  public void setRounds(int rounds){
    this.rounds = rounds;
  }

  public Map<Long, Integer> getDistances(){
   return distances;
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
   
 
  public lobbyStates getState(){
    return state;
  }   

  public void setState(lobbyStates state ){
    this.state = state;
  }

  public Map<Long, Integer> getCurrRounds(){
    return currRound;
  }
 
  /**
   * note that player is in this game since he knows the lobbyId
   * @param score
   * @param playerId
  */
  public void setDistance(int dist,Long playerId){
    distances.put(playerId, dist);
  }

   
}
 