package ch.uzh.ifi.hase.soprafs24.controller;
import ch.uzh.ifi.hase.soprafs24.controller.chat.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class WebSocketController {

    @MessageMapping("/send/GameMode1/coordinates/{lobbyId}")
    @SendTo("/topic/lobby/GameMode1/coordinates/{lobbyId}")
    public Map<String,String> sendCoordinates(@DestinationVariable String lobbyId, @Payload Map<String,String> coordinates) {
        // Convert coordinates to a String format for message sending
        coordinates.put("lobbyId",lobbyId.toString());

        return coordinates;
    }

    @MessageMapping("/send/GameMode1/LeaderBoard/{lobbyId}")
    @SendTo("/topic/lobby/GameMode1/LeaderBoard/{lobbyId}")
    public Map<String,String> sendLeaderBoard(@DestinationVariable String lobbyId, @Payload Map<String,String> leaderBoard) {
        // Convert coordinates to a String format for message sending
        leaderBoard.put("lobbyId",lobbyId.toString());

        return leaderBoard;
    }
    @MessageMapping("chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message) {return message;}

    @MessageMapping("chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        return message;
    }
}
