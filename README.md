# ChatWave - Real-Time Chat Application

A multi-threaded, real-time chat application built with Java sockets, featuring both GUI and console interfaces.

## Features

- **Real-time messaging** between multiple clients
- **User authentication** with unique usernames
- **Timestamp display** for all messages
- **GUI and Console interfaces** for different user preferences
- **File sharing support** with size limits
- **Multi-threaded server** handling concurrent clients
- **Command system** for user interaction
- **Automatic reconnection** handling

## Project Structure

```
ChatWave/
├── src/
│   ├── server/
│   │   ├── ChatServer.java      # Main server class
│   │   └── ClientHandler.java   # Individual client handler
│   ├── client/
│   │   ├── ChatClient.java      # Main client class
│   │   ├── ChatClientGUI.java   # Swing-based GUI
│   │   ├── ConsoleChat.java     # Console interface
│   │   └── MessageReceiver.java # Message receiving thread
│   └── shared/
│       ├── Message.java         # Message data structure
│       └── Constants.java       # Shared constants
├── files/                       # File sharing directory
├── compile.bat                  # Windows compilation script
├── run-server.bat              # Windows server launcher
├── run-client.bat              # Windows client launcher
└── README.md                   # This file
```

## How to Run

### Prerequisites
- Java 8 or higher
- Windows OS (scripts provided for Windows, but can be adapted for other OS)

### Compilation
```bash
./compile.bat
```

### Running the Server
```bash
./run-server.bat [port]
```
Default port is 12345 if not specified.

### Running the Client

#### GUI Mode (Default)
```bash
./run-client.bat
```

#### Console Mode
```bash
./run-client.bat -console
```

#### With Custom Parameters
```bash
./run-client.bat -host localhost -port 12345 -username YourName
```

## Available Commands

- `/users` - List all online users
- `/help` - Show available commands
- `/leave` - Leave the chat
- File sharing through GUI "Send File" button

## Technical Implementation

### Server Architecture
- **Multi-threaded design** using `ServerSocket` and individual `ClientHandler` threads
- **Concurrent data structures** for thread-safe client management
- **Broadcast messaging** to all connected clients
- **File handling** with size restrictions and safe storage

### Client Architecture
- **Separate threads** for sending and receiving messages
- **Event-driven GUI** using Swing components
- **Robust error handling** and reconnection logic
- **File selection and transfer** capabilities

### Message Protocol
- **Serialized objects** for reliable data transmission
- **Message types** for different content (text, file, system messages)
- **Timestamp integration** for chronological message ordering

## Skills Demonstrated

1. **Socket Programming** - TCP client-server communication
2. **Multithreading** - Concurrent client handling and message processing
3. **GUI Development** - Swing-based user interface
4. **File I/O** - File sharing and storage management
5. **Object Serialization** - Network data transmission
6. **Error Handling** - Robust exception management
7. **Design Patterns** - Observer pattern for message handling

## Customization Ideas

- Add private messaging between users
- Implement chat rooms/channels
- Add message encryption for security
- Create user profiles and avatars
- Add emoji support
- Implement message history persistence
- Add audio/video call features
- Create mobile client versions

## License

This project is created for educational purposes. Feel free to modify and extend it for your learning needs.
