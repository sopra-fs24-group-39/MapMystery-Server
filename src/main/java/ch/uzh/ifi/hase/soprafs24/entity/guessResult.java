package ch.uzh.ifi.hase.soprafs24.entity;

public class guessResult {
  private float distance;
  private float timeDelta;

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
}
