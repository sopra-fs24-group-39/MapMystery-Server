package ch.uzh.ifi.hase.soprafs24.controller.Chat;

import ch.uzh.ifi.hase.soprafs24.controller.chat.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTypeTest {

    @Test
    public void testMessageTypeValues() {
        MessageType[] messageTypes = MessageType.values();

        assertNotNull(messageTypes);
        assertEquals(3, messageTypes.length);
        assertTrue(contains(messageTypes, MessageType.CHAT));
        assertTrue(contains(messageTypes, MessageType.JOIN));
        assertTrue(contains(messageTypes, MessageType.LEAVE));
    }

    @Test
    public void testValueOf() {
        assertEquals(MessageType.CHAT, MessageType.valueOf("CHAT"));
        assertEquals(MessageType.JOIN, MessageType.valueOf("JOIN"));
        assertEquals(MessageType.LEAVE, MessageType.valueOf("LEAVE"));
    }

    private boolean contains(MessageType[] values, MessageType value) {
        for (MessageType v : values) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }
}