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
    if( Distance > 2000*1000){
      return 0;
    }
    else {
      return (2400*1000 - Distance)/1000;
    }
  }
  
  @Override
  public void setPoints(float distance,float timeDelta,Long playerId){
    if(distance < 0.0){
      this.points.put(playerId, 0.0f);
    }
    else{
      float prevPoints = this.getPoints().getOrDefault(playerId, 0.0f);
      prevPoints += this.computePoints(distance);
      this.points.put(playerId,prevPoints);
    }
    
  }

  
}
