package shared;

/**
 * Constants shared between client and server
 */
public class Constants {
    public static final int DEFAULT_PORT = 12345;
    public static final String DEFAULT_HOST = "localhost";
    public static final int MAX_MESSAGE_SIZE = 1024 * 1024; // 1MB
    public static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String FILES_DIRECTORY = "files";
    
    // Protocol commands
    public static final String COMMAND_JOIN = "/join";
    public static final String COMMAND_LEAVE = "/leave";
    public static final String COMMAND_LIST_USERS = "/users";
    public static final String COMMAND_SEND_FILE = "/sendfile";
    public static final String COMMAND_HELP = "/help";
    
    // Server messages
    public static final String SERVER_NAME = "SERVER";
    public static final String WELCOME_MESSAGE = "Welcome to ChatWave! Type /help for commands.";
    public static final String GOODBYE_MESSAGE = "Thanks for using ChatWave!";
}
