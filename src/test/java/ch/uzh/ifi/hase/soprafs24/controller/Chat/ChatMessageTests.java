package ch.uzh.ifi.hase.soprafs24.controller.Chat;

import ch.uzh.ifi.hase.soprafs24.controller.chat.ChatMessage;
import ch.uzh.ifi.hase.soprafs24.controller.chat.MessageType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChatMessageTests {

    @Test
    public void testChatMessageConstructorAndGetters() {
        String content = "Hello, World!";
        String sender = "User1";
        MessageType type = MessageType.CHAT;

        ChatMessage chatMessage = new ChatMessage(content, sender, type);

        assertNotNull(chatMessage);
        assertEquals(content, chatMessage.getContent());
        assertEquals(sender, chatMessage.getSender());
        assertEquals(type, chatMessage.getType());
    }

    @Test
    public void testChatMessageSetters() {
        ChatMessage chatMessage = new ChatMessage("Initial Content", "Initial Sender", MessageType.CHAT);

        String newContent = "New Content";
        String newSender = "New Sender";
        MessageType newType = MessageType.LEAVE;

        chatMessage.setContent(newContent);
        chatMessage.setSender(newSender);
        chatMessage.setType(newType);

        assertEquals(newContent, chatMessage.getContent());
        assertEquals(newSender, chatMessage.getSender());
        assertEquals(newType, chatMessage.getType());
    }
}
