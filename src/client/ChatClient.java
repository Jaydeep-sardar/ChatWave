package client;

import shared.Message;
import shared.Constants;
import java.io.*;
import java.net.Socket;
import java.net.ConnectException;

/**
 * Chat client that connects to the server and handles communication
 */
public class ChatClient {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String username;
    private String host;
    private int port;
    private boolean isConnected;
    private ChatClientGUI gui;
    private MessageReceiver messageReceiver;
    
    public ChatClient(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.isConnected = false;
    }
    
    public boolean connect() {
        try {
            socket = new Socket(host, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            
            // Send username to server
            Message usernameMessage = new Message(username, username, Message.MessageType.TEXT);
            output.writeObject(usernameMessage);
            output.flush();
            
            isConnected = true;
            
            // Start message receiver thread
            messageReceiver = new MessageReceiver(input, this);
            new Thread(messageReceiver).start();
            
            return true;
        } catch (ConnectException e) {
            System.err.println("Could not connect to server at " + host + ":" + port);
            System.err.println("Make sure the server is running.");
            return false;
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }
    
    public void disconnect() {
        if (isConnected) {
            isConnected = false;
            
            try {
                if (messageReceiver != null) {
                    messageReceiver.stop();
                }
                
                if (output != null) output.close();
                if (input != null) input.close();
                if (socket != null) socket.close();
                
                System.out.println("Disconnected from server");
            } catch (IOException e) {
                System.err.println("Error disconnecting: " + e.getMessage());
            }
        }
    }
    
    public void sendMessage(String content) {
        if (isConnected && content != null && !content.trim().isEmpty()) {
            try {
                Message message = new Message(content, username, Message.MessageType.TEXT);
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                System.err.println("Error sending message: " + e.getMessage());
                disconnect();
            }
        }
    }
    
    public void sendFile(String filename, byte[] fileData) {
        if (isConnected && filename != null && fileData != null) {
            try {
                if (fileData.length > Constants.MAX_FILE_SIZE) {
                    if (gui != null) {
                        gui.displayMessage(new Message("File too large. Maximum size is " + 
                                                     (Constants.MAX_FILE_SIZE / 1024 / 1024) + "MB", 
                                                     Constants.SERVER_NAME, 
                                                     Message.MessageType.SERVER_MESSAGE));
                    }
                    return;
                }
                
                Message fileMessage = new Message(filename, fileData, username);
                output.writeObject(fileMessage);
                output.flush();
            } catch (IOException e) {
                System.err.println("Error sending file: " + e.getMessage());
                disconnect();
            }
        }
    }
    
    public void handleReceivedMessage(Message message) {
        if (gui != null) {
            gui.displayMessage(message);
        } else {
            // Console mode
            System.out.println(message.toString());
        }
    }
    
    public void handleDisconnection() {
        isConnected = false;
        if (gui != null) {
            gui.handleDisconnection();
        }
    }
    
    public void setGUI(ChatClientGUI gui) {
        this.gui = gui;
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    public String getUsername() {
        return username;
    }
    
    public static void main(String[] args) {
        String host = Constants.DEFAULT_HOST;
        int port = Constants.DEFAULT_PORT;
        String username = "User" + System.currentTimeMillis() % 1000;
        
        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-host":
                case "-h":
                    if (i + 1 < args.length) {
                        host = args[++i];
                    }
                    break;
                case "-port":
                case "-p":
                    if (i + 1 < args.length) {
                        try {
                            port = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid port number: " + args[i]);
                        }
                    }
                    break;
                case "-username":
                case "-u":
                    if (i + 1 < args.length) {
                        username = args[++i];
                    }
                    break;
                case "-console":
                case "-c":
                    // Console mode
                    ChatClient consoleClient = new ChatClient(host, port, username);
                    if (consoleClient.connect()) {
                        ConsoleChat consoleChat = new ConsoleChat(consoleClient);
                        consoleChat.start();
                    }
                    return;
            }
        }
        
        // Default to GUI mode
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ChatClientGUI(host, port, username);
        });
    }
}
