package client;

import shared.Message;
import shared.Constants;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;

/**
 * GUI interface for the chat client using Swing
 */
public class ChatClientGUI extends JFrame {
    private ChatClient client;
    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton sendFileButton;
    private JButton connectButton;
    private JTextField hostField;
    private JTextField portField;
    private JTextField usernameField;
    private JPanel connectionPanel;
    private JPanel chatPanel;
    private StyledDocument doc;
    
    public ChatClientGUI(String defaultHost, int defaultPort, String defaultUsername) {
        super("ChatWave - Real-Time Chat Application");
        
        initializeGUI(defaultHost, defaultPort, defaultUsername);
        setupEventHandlers();
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeGUI(String defaultHost, int defaultPort, String defaultUsername) {
        setLayout(new BorderLayout());
        
        // Create connection panel
        createConnectionPanel(defaultHost, defaultPort, defaultUsername);
        
        // Create chat panel
        createChatPanel();
        
        // Initially show connection panel
        add(connectionPanel, BorderLayout.CENTER);
    }
    
    private void createConnectionPanel(String defaultHost, int defaultPort, String defaultUsername) {
        connectionPanel = new JPanel(new GridBagLayout());
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Server Connection"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Host
        gbc.gridx = 0; gbc.gridy = 0;
        connectionPanel.add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        hostField = new JTextField(defaultHost, 15);
        connectionPanel.add(hostField, gbc);
        
        // Port
        gbc.gridx = 0; gbc.gridy = 1;
        connectionPanel.add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        portField = new JTextField(String.valueOf(defaultPort), 15);
        connectionPanel.add(portField, gbc);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 2;
        connectionPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(defaultUsername, 15);
        connectionPanel.add(usernameField, gbc);
        
        // Connect button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(100, 30));
        connectionPanel.add(connectButton, gbc);
        
        // Logo/Title
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("ChatWave", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        connectionPanel.add(titleLabel, gbc);
        
        gbc.gridy = 5;
        JLabel subtitleLabel = new JLabel("Real-Time Chat Application", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.GRAY);
        connectionPanel.add(subtitleLabel, gbc);
    }
    
    private void createChatPanel() {
        chatPanel = new JPanel(new BorderLayout());
        
        // Chat area
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        doc = chatArea.getStyledDocument();
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(750, 400));
        
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 12));
        sendButton = new JButton("Send");
        sendFileButton = new JButton("Send File");
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(sendButton);
        buttonPanel.add(sendFileButton);
        
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("Not connected");
        statusPanel.add(statusLabel);
        chatPanel.add(statusPanel, BorderLayout.NORTH);
    }
    
    private void setupEventHandlers() {
        // Connect button
        connectButton.addActionListener(e -> connectToServer());
        
        // Enter key in username field
        usernameField.addActionListener(e -> connectToServer());
        
        // Send button
        sendButton.addActionListener(e -> sendMessage());
        
        // Enter key in message field
        messageField.addActionListener(e -> sendMessage());
        
        // Send file button
        sendFileButton.addActionListener(e -> sendFile());
        
        // Window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null) {
                    client.disconnect();
                }
                System.exit(0);
            }
        });
    }
    
    private void connectToServer() {
        String host = hostField.getText().trim();
        String portText = portField.getText().trim();
        String username = usernameField.getText().trim();
        
        if (host.isEmpty() || portText.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid port number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        client = new ChatClient(host, port, username);
        client.setGUI(this);
        
        if (client.connect()) {
            // Switch to chat view
            remove(connectionPanel);
            add(chatPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
            
            messageField.requestFocus();
            
            // Update title
            setTitle("ChatWave - " + username + " @ " + host + ":" + port);
            
            displaySystemMessage("Connected to " + host + ":" + port + " as " + username);
        } else {
            JOptionPane.showMessageDialog(this, "Could not connect to server.\\nMake sure the server is running.", 
                                        "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && client != null && client.isConnected()) {
            client.sendMessage(message);
            messageField.setText("");
        }
    }
    
    private void sendFile() {
        if (client == null || !client.isConnected()) {
            JOptionPane.showMessageDialog(this, "Not connected to server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File to Send");
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            try {
                if (selectedFile.length() > Constants.MAX_FILE_SIZE) {
                    JOptionPane.showMessageDialog(this, 
                        "File too large. Maximum size is " + (Constants.MAX_FILE_SIZE / 1024 / 1024) + "MB.", 
                        "File Too Large", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                byte[] fileData = Files.readAllBytes(selectedFile.toPath());
                client.sendFile(selectedFile.getName(), fileData);
                
                displaySystemMessage("Sending file: " + selectedFile.getName());
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage(), 
                                            "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void displayMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            try {
                Style style = null;
                Color color = Color.BLACK;
                
                switch (message.getType()) {
                    case SERVER_MESSAGE:
                        color = new Color(70, 130, 180);
                        break;
                    case USER_JOIN:
                        color = new Color(34, 139, 34);
                        break;
                    case USER_LEAVE:
                        color = new Color(220, 20, 60);
                        break;
                    case FILE:
                        color = new Color(255, 140, 0);
                        break;
                    default:
                        color = Color.BLACK;
                        break;
                }
                
                style = chatArea.addStyle("MessageStyle", null);
                StyleConstants.setForeground(style, color);
                
                String timestamp = message.getFormattedTimestamp();
                String formattedMessage = String.format("[%s] %s: %s%n", 
                                                      timestamp, 
                                                      message.getUsername(), 
                                                      message.getContent());
                
                doc.insertString(doc.getLength(), formattedMessage, style);
                
                // Auto-scroll to bottom
                chatArea.setCaretPosition(doc.getLength());
                
            } catch (BadLocationException e) {
                System.err.println("Error displaying message: " + e.getMessage());
            }
        });
    }
    
    private void displaySystemMessage(String message) {
        Message systemMessage = new Message(message, "SYSTEM", Message.MessageType.SERVER_MESSAGE);
        displayMessage(systemMessage);
    }
    
    public void handleDisconnection() {
        SwingUtilities.invokeLater(() -> {
            displaySystemMessage("Disconnected from server");
            
            // Show reconnection dialog
            int option = JOptionPane.showConfirmDialog(this, 
                "Connection lost. Would you like to reconnect?", 
                "Disconnected", 
                JOptionPane.YES_NO_OPTION);
                
            if (option == JOptionPane.YES_OPTION) {
                // Return to connection panel
                remove(chatPanel);
                add(connectionPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
                setTitle("ChatWave - Real-Time Chat Application");
            } else {
                System.exit(0);
            }
        });
    }
}
