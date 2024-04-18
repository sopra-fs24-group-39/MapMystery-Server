package ch.uzh.ifi.hase.soprafs24.controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class GameController {

    @MessageMapping("/send/{lobbyId}")
    @SendTo("/topic/game/{lobbyId}")
    public Map<String,String> sendCoordinates(@DestinationVariable String lobbyId, @Payload Map<String,String> coordinates) {
        // Convert coordinates to a String format for message sending
        coordinates.put("lobbyId",lobbyId.toString());

        return coordinates;
    }
}
