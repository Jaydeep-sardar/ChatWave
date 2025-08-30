package client;

import shared.Message;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Handles receiving messages from the server in a separate thread
 */
public class MessageReceiver implements Runnable {
    private ObjectInputStream input;
    private ChatClient client;
    private boolean isRunning;
    
    public MessageReceiver(ObjectInputStream input, ChatClient client) {
        this.input = input;
        this.client = client;
        this.isRunning = true;
    }
    
    @Override
    public void run() {
        while (isRunning) {
            try {
                Message message = (Message) input.readObject();
                if (message != null) {
                    client.handleReceivedMessage(message);
                }
            } catch (EOFException e) {
                // Server closed connection
                break;
            } catch (IOException | ClassNotFoundException e) {
                if (isRunning) {
                    System.err.println("Error receiving message: " + e.getMessage());
                }
                break;
            }
        }
        
        client.handleDisconnection();
    }
    
    public void stop() {
        isRunning = false;
    }
}
