/**
 * author: david 
 */

 package ch.uzh.ifi.hase.soprafs24.entity;
 import java.util.ArrayList;
 import javax.persistence.*;
 import java.util.Map;
 import java.util.HashMap;
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.GeneratedValue;

import org.springframework.data.geo.Point;
 
 @Entity
 @Table(name = "LOBBY")
 public class Lobby {
   @Id
   @GeneratedValue
   private Long lobbyId;
   @Column
   private ArrayList<User> players;
   @Column
   private int playerLimit = 3;
   @Column
   private String state;
   @Column
   private int rounds = 5;
   @Column
   private ArrayList<List<Double>> coordinates = new ArrayList<>();
   
   @ElementCollection
   @CollectionTable(name = "game_rounds", joinColumns = @JoinColumn(name = "game_id"))
   @MapKeyColumn(name = "player_id")
   @Column(name = "current_round")
   private Map<Long, Integer> currRound = new HashMap<>();  
 
   @ElementCollection
   @CollectionTable(name = "lobby_points", joinColumns = @JoinColumn(name = "lobby_id"))
   @MapKeyColumn(name = "player_id")
   @Column(name = "points")
   private Map<Long, Integer> points = new HashMap<>();
   
   
 
   public Lobby(){
     this.players = new ArrayList<>();
     this.state = "open";
     
   }
 
   public Long getId(){
     return lobbyId;
   }
 
   public List<User> getPlayers(){
    return players;
   }

   public Map<Long, Integer> getPoints(){
    return points;
   }

   public Map<Long,Integer> getCurrRound(){
    return currRound;
   }
 
   public void addPlayer(User user) throws Exception{
 
     int numberOfMembers = this.players.size();
 
     if (numberOfMembers < this.playerLimit ) {
       this.players.add(user);
       this.currRound.put(user.getId(),1);
       this.points.put(user.getId(),0);
     } 
     
     if (this.state == "open" && numberOfMembers +1 >= this.playerLimit) {
       this.state = "playing";
       try{
         initGame();
       }
       catch (Exception e){
         throw new Exception(e.getMessage());
       }
     }
   } 
 
   public String getState(){
     return state;
   }
 
   public void removePlayer(User user) throws Exception {
     try {
       boolean removed = this.players.remove(user);
       if (!removed) {
         throw new Exception("User not found in members list");
       }
     } catch (Exception e) {
         throw new Exception("Failed to remove member: " + e.getMessage());
       }
   }
 
   private void initGame() throws  Exception{
     
   }
 
   private boolean checkGameState(){
     for (int k = 0; k < this.players.size(); k++){ 
       Long playerId = this.players.get(k).getId();
       if(currRound.get(playerId) <5){
         return false;
       }
     }
     return true;
   }
 
   /**
    * note that player is in this game since he knows the lobbyId
    * @param score
    * @param playerId
    */
   public void setScore(int score,Long playerId){
     points.put(playerId, score);
   }
 
   public void advanceRound(Long playerId){
     int oldRound = currRound.get(playerId);
     if(oldRound < this.rounds){
       currRound.put(playerId, ++oldRound);
     }
 
     if (this.checkGameState()){
       this.endGame();
     }
     else if (this.checkNextRound()){
       // send the cooordinates to each player
       
     }
 
   }
 
     /*
      * this.players cannot be empty otherwies the game would not
      * have been initialized
      */
   public boolean checkNextRound(){
     Long playerId = this.players.get(0).getId();
     int currentRound = currRound.get(playerId);
 
     for (int k = 1; k < this.players.size(); k++){ 
       playerId = this.players.get(k).getId();
       int currentRound2 = currRound.get(playerId);
 
       if(currentRound != currentRound2 ){
         return false;
       }
 
       currentRound = currentRound2;
     }
     return true;
   }
 
   /**
    * sends the report of game to each player
    */
   public void endGame(){
     this.state = "finished";
   }
 }
 