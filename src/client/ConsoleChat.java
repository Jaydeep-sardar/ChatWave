package client;

import java.util.Scanner;

/**
 * Console-based chat interface for text-only interaction
 */
public class ConsoleChat {
    private ChatClient client;
    private Scanner scanner;
    
    public ConsoleChat(ChatClient client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        System.out.println("=== ChatWave Console Client ===");
        System.out.println("Connected as: " + client.getUsername());
        System.out.println("Type your messages and press Enter. Type '/help' for commands or '/leave' to quit.");
        System.out.println("=====================================");
        
        while (client.isConnected()) {
            try {
                String input = scanner.nextLine();
                if (input != null) {
                    if (input.equals("/leave") || input.equals("/quit") || input.equals("/exit")) {
                        break;
                    }
                    client.sendMessage(input);
                }
            } catch (Exception e) {
                System.err.println("Error reading input: " + e.getMessage());
                break;
            }
        }
        
        client.disconnect();
        scanner.close();
        System.out.println("Goodbye!");
    }
}
