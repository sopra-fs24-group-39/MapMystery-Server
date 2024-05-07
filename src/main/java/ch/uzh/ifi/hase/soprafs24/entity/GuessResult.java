package ch.uzh.ifi.hase.soprafs24.entity;

public class GuessResult {
  private float distance;
  private float timeDelta;
  private Long playerId;


  public void setDistance(float distance){
    this.distance = distance;
  }
  public float getDistance(){
    return distance;
  }

  public void setTimeDelta(float timeDelta){
    this.timeDelta = timeDelta;
  }

  public float getTimeDelta(){
    return timeDelta;
  }

  public Long getPlayerId(){
    return playerId;
  }

  public void setPlayerId(Long playerId){
    this.playerId = playerId;
  }
}
