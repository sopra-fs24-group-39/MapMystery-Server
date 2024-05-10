package ch.uzh.ifi.hase.soprafs24.controller.chat;

public class ChatMessage {

    private String content;
    private String sender;
    private MessageType type;

    // Constructor with all fields
    public ChatMessage(String content, String sender, MessageType type) {
        this.content = content;
        this.sender = sender;
        this.type = type;
    }

    // Getter for content
    public String getContent() {
        return content;
    }

    // Setter for content
    public void setContent(String content) {
        this.content = content;
    }

    // Getter for sender
    public String getSender() {
        return sender;
    }

    // Setter for sender
    public void setSender(String sender) {
        this.sender = sender;
    }

    // Getter for type
    public MessageType getType() {
        return type;
    }

    // Setter for type
    public void setType(MessageType type) {
        this.type = type;
    }
}
