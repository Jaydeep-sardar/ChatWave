package server;

import shared.Message;
import shared.Constants;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Multi-threaded chat server that handles multiple clients
 */
public class ChatServer {
    private ServerSocket serverSocket;
    private final Map<String, ClientHandler> clients;
    private final Set<String> usernames;
    private boolean isRunning;
    private final int port;
    
    public ChatServer(int port) {
        this.port = port;
        this.clients = new ConcurrentHashMap<>();
        this.usernames = ConcurrentHashMap.newKeySet();
        this.isRunning = false;
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            System.out.println("ChatWave Server started on port " + port);
            System.out.println("Waiting for clients to connect...");
            
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());
                    
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
    
    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            // Disconnect all clients
            for (ClientHandler client : clients.values()) {
                client.disconnect();
            }
            clients.clear();
            usernames.clear();
            
            System.out.println("Server stopped.");
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
    
    public synchronized boolean addClient(String username, ClientHandler clientHandler) {
        if (usernames.contains(username)) {
            return false; // Username already taken
        }
        
        clients.put(username, clientHandler);
        usernames.add(username);
        
        // Notify all clients about new user
        Message joinMessage = new Message(username + " joined the chat", 
                                        Constants.SERVER_NAME, 
                                        Message.MessageType.USER_JOIN);
        broadcastMessage(joinMessage, null);
        
        // Send welcome message to new user
        Message welcomeMessage = new Message(Constants.WELCOME_MESSAGE, 
                                           Constants.SERVER_NAME, 
                                           Message.MessageType.SERVER_MESSAGE);
        clientHandler.sendMessage(welcomeMessage);
        
        // Send list of current users
        sendUserList(clientHandler);
        
        System.out.println("User '" + username + "' joined. Total users: " + clients.size());
        return true;
    }
    
    public synchronized void removeClient(String username) {
        if (clients.containsKey(username)) {
            clients.remove(username);
            usernames.remove(username);
            
            // Notify all clients about user leaving
            Message leaveMessage = new Message(username + " left the chat", 
                                             Constants.SERVER_NAME, 
                                             Message.MessageType.USER_LEAVE);
            broadcastMessage(leaveMessage, null);
            
            System.out.println("User '" + username + "' left. Total users: " + clients.size());
        }
    }
    
    public void broadcastMessage(Message message, String excludeUser) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (excludeUser == null || !entry.getKey().equals(excludeUser)) {
                entry.getValue().sendMessage(message);
            }
        }
    }
    
    public void sendUserList(ClientHandler clientHandler) {
        StringBuilder userList = new StringBuilder("Online users: ");
        for (String username : usernames) {
            userList.append(username).append(", ");
        }
        if (userList.length() > 14) {
            userList.setLength(userList.length() - 2); // Remove last comma and space
        }
        
        Message userListMessage = new Message(userList.toString(), 
                                            Constants.SERVER_NAME, 
                                            Message.MessageType.SERVER_MESSAGE);
        clientHandler.sendMessage(userListMessage);
    }
    
    public Set<String> getUsernames() {
        return new HashSet<>(usernames);
    }
    
    public static void main(String[] args) {
        int port = Constants.DEFAULT_PORT;
        
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port: " + Constants.DEFAULT_PORT);
            }
        }
        
        ChatServer server = new ChatServer(port);
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        
        server.start();
    }
}
