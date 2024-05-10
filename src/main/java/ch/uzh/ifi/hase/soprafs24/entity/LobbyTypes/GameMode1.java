package ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes;

import javax.persistence.Entity;

import ch.uzh.ifi.hase.constants.GameModes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static java.lang.Math.ceil;


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
     *
     * @param Distance
     * @return
     */
    public float computePoints(float Distance, float timeDelta) {
        float maxDistance = 40075.017f *1000/2; // Maximum distance around the world in meters
        float maxTimeDelta = 130.0f; // Maximum time delta in seconds + 10 seconds to account for connection
        float maxPoints = 100.0f; // Maximum points to award

        float distanceSensitivity;
        if (Distance < 1000.0f*1000) {
            distanceSensitivity = 1.0f - 0.10f*Distance/1000.0f*1000; // the 0.10f to reward the players for their near guess
        } else {
            distanceSensitivity = 1.0f - Distance/maxDistance; // Linear Decrease for any Distance bigger than the average countrysize
        }

        // Calculate time sensitivity based on time delta
        float timeSensitivity = 1.0f - (timeDelta / maxTimeDelta);

        // Combine distance and time sensitivity to calculate points
        float points = (float) Math.ceil(maxPoints * distanceSensitivity * timeSensitivity);

        return points;
    }
    public float computePoints1(float Distance,float Timedelta){
        if( Distance > 2000*1000){
            return 0;
        }
        else {
            return (2400*1000 - Distance)/1000;
        }
    }

    @Override
    public void setPoints(float distance, float timeDelta, Long playerId) {
        if(distance > (40075.017*1000)/2){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The maximum distance between any two points on earth is " + (40075.017*1000)/2 + " but the actual distance parsed is " + distance);
        }
        if(timeDelta > 130){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Any guess should be submitted within 130 seconds, but some guess exceed it : "+ timeDelta);
        }

        if (distance < 0.0) {
            this.points.put(playerId, 0.0f);
        }
        else {
            float prevPoints = this.getPoints().getOrDefault(playerId, 0.0f);
            prevPoints += this.computePoints1(distance, timeDelta);
            this.points.put(playerId, prevPoints);
        }

    }


}
