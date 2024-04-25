package ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes;

import javax.persistence.Entity;
import ch.uzh.ifi.hase.constants.GameModes;


@Entity
public class GameMode1 extends Lobby {
  private static final long serialVersionUID = 1L;

  
  {
    // Initializer block to set default values
    super.playerLimit = 3;
    super.rounds = 5;
    super.gamemode = GameModes.Gamemode1;
  }

  /**
   * assuming distance is in meters
   * @param Distance
   * @return
   */
  public float computePoints(float Distance){
    if( Distance > 1200*1000){
      return 0;
    }
    else {
      return (1400*1000 - Distance)/1000;
    }
  }

  @Override
  public void setPoints(float points,Long playerId){
    if(points < 0.0){
      this.points.put(playerId, 0.0f);
    }
    else{
      float prevPoints = this.getPoints().getOrDefault(playerId, 0.0f);
      prevPoints += this.computePoints(points);
      this.points.put(playerId,prevPoints);
    }
    
  }

  
}
