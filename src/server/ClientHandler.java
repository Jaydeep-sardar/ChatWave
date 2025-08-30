package server;

import shared.Message;
import shared.Constants;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Handles individual client connections in separate threads
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ChatServer server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String username;
    private boolean isConnected;
    
    public ClientHandler(Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.isConnected = true;
    }
    
    @Override
    public void run() {
        try {
            // Initialize streams
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());
            
            // Get username from client
            Message usernameMessage = (Message) input.readObject();
            username = usernameMessage.getContent();
            
            // Try to add client to server
            if (!server.addClient(username, this)) {
                Message errorMessage = new Message("Username '" + username + "' is already taken. Please try again.", 
                                                 Constants.SERVER_NAME, 
                                                 Message.MessageType.SERVER_MESSAGE);
                sendMessage(errorMessage);
                disconnect();
                return;
            }
            
            // Handle client messages
            while (isConnected) {
                try {
                    Message message = (Message) input.readObject();
                    handleMessage(message);
                } catch (EOFException | ClassNotFoundException e) {
                    break; // Client disconnected
                } catch (IOException e) {
                    if (isConnected) {
                        System.err.println("Error reading message from " + username + ": " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling client " + username + ": " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    private void handleMessage(Message message) {
        String content = message.getContent();
        
        if (content.startsWith("/")) {
            handleCommand(content);
        } else if (message.getType() == Message.MessageType.FILE) {
            handleFileMessage(message);
        } else {
            // Regular text message - broadcast to all clients
            message.setUsername(username);
            server.broadcastMessage(message, null);
        }
    }
    
    private void handleCommand(String command) {
        String[] parts = command.split(" ", 2);
        String cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case Constants.COMMAND_LIST_USERS:
                server.sendUserList(this);
                break;
                
            case Constants.COMMAND_HELP:
                sendHelpMessage();
                break;
                
            case Constants.COMMAND_LEAVE:
                disconnect();
                break;
                
            default:
                Message errorMessage = new Message("Unknown command: " + cmd + ". Type /help for available commands.", 
                                                 Constants.SERVER_NAME, 
                                                 Message.MessageType.SERVER_MESSAGE);
                sendMessage(errorMessage);
                break;
        }
    }
    
    private void handleFileMessage(Message message) {
        try {
            // Save file to server's files directory
            String filename = message.getFilename();
            byte[] fileData = message.getFileData();
            
            File filesDir = new File(Constants.FILES_DIRECTORY);
            if (!filesDir.exists()) {
                filesDir.mkdirs();
            }
            
            String safePath = Constants.FILES_DIRECTORY + File.separator + 
                            System.currentTimeMillis() + "_" + filename;
            Files.write(Paths.get(safePath), fileData);
            
            // Broadcast file message to all clients
            message.setUsername(username);
            server.broadcastMessage(message, null);
            
            System.out.println("File '" + filename + "' received from " + username + 
                             " and saved as " + safePath);
            
        } catch (IOException e) {
            System.err.println("Error handling file from " + username + ": " + e.getMessage());
            Message errorMessage = new Message("Error processing file: " + e.getMessage(), 
                                             Constants.SERVER_NAME, 
                                             Message.MessageType.SERVER_MESSAGE);
            sendMessage(errorMessage);
        }
    }
    
    private void sendHelpMessage() {
        StringBuilder help = new StringBuilder();
        help.append("Available commands:\\n");
        help.append("/users - List online users\\n");
        help.append("/help - Show this help message\\n");
        help.append("/leave - Leave the chat\\n");
        help.append("\\nTo send a file, use the 'Send File' button in the GUI");
        
        Message helpMessage = new Message(help.toString(), 
                                        Constants.SERVER_NAME, 
                                        Message.MessageType.SERVER_MESSAGE);
        sendMessage(helpMessage);
    }
    
    public void sendMessage(Message message) {
        try {
            if (output != null && isConnected) {
                output.writeObject(message);
                output.flush();
            }
        } catch (IOException e) {
            System.err.println("Error sending message to " + username + ": " + e.getMessage());
            disconnect();
        }
    }
    
    public void disconnect() {
        if (isConnected) {
            isConnected = false;
            
            try {
                if (username != null) {
                    server.removeClient(username);
                }
                
                if (input != null) input.close();
                if (output != null) output.close();
                if (clientSocket != null) clientSocket.close();
                
                System.out.println("Client " + (username != null ? username : "unknown") + " disconnected");
            } catch (IOException e) {
                System.err.println("Error disconnecting client: " + e.getMessage());
            }
        }
    }
    
    public String getUsername() {
        return username;
    }
    
    public boolean isConnected() {
        return isConnected;
    }
}
