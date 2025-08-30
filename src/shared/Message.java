package shared;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Message class to represent chat messages with timestamp and user information
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        TEXT, FILE, USER_JOIN, USER_LEAVE, SERVER_MESSAGE
    }
    
    private String content;
    private String username;
    private LocalDateTime timestamp;
    private MessageType type;
    private String filename; // for file messages
    private byte[] fileData; // for file messages
    
    public Message(String content, String username, MessageType type) {
        this.content = content;
        this.username = username;
        this.timestamp = LocalDateTime.now();
        this.type = type;
    }
    
    public Message(String filename, byte[] fileData, String username) {
        this.filename = filename;
        this.fileData = fileData;
        this.username = username;
        this.timestamp = LocalDateTime.now();
        this.type = MessageType.FILE;
        this.content = "Sent file: " + filename;
    }
    
    // Getters and setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public byte[] getFileData() {
        return fileData;
    }
    
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
    
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", getFormattedTimestamp(), username, content);
    }
}
