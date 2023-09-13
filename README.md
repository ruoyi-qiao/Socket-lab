# Simple Socket Chat Server and Client
This repository contains a simple implementation of a chat server and client using Java sockets. The project consists of two main classes: Server and Client.

## Features
* Multi-client Support: The server can handle multiple client connections concurrently.
* Broadcast Messages: The server can broadcast messages sent by one client to all connected clients.
* Unicast Messages: The server can send messages to a specific client based on their unique identifier (ID).
* File Reading and Transmission: Clients can transmit messages to server by reading files from the local file system.

## Basic Model

![](https://github.com/Ruoyisius/BasicSocket/blob/main/images/ThreadRelation.png?raw=true)

## Server
The Server class represents the chat server. It listens on a specified port for incoming client connections and manages multiple client connections simultaneously. Each connected client runs in its own thread, allowing for concurrent communication with clients.

### Usage
To run the server, you can specify the port as a command-line argument when starting the server:

```bash
java Server <port>
```

Alternatively, if no port is provided as an argument, the server will prompt you to enter the port number.

Once the server is running, it will display messages about client connections and messages received from clients.

To send message to **single** client, you can use the id of the client to specify the client that you want to send to.
```bash
<id>:<message>
```
To broadcast to **all clients**, you can use `*` to launch broadcast.
```bash
*:<message>
```


## Client
The Client class represents a chat client. It connects to the server using the server's IP address and port number. The client can send messages to the server, which are then broadcast to all connected clients.

### Usage
To run the client, you can specify the server's IP address and port number as command-line arguments when starting the client:

```bash
java Client <server_ip_address> <port>
```
Alternatively, if no IP address and port are provided as arguments, the client will prompt you to enter them.

Once the client is connected to the server, you can enter messages in the console to send to the server.

To send message to server, you can do this by entering the message and enter '\n' **twice**.

```bash
<message>
```

Clients can transmit messages to server by reading files from the local file system. To do this, specify the file's path as a message. 

```bash
-F <path-to-file>
```

## Usage Example
1. Start the server:
```bash
java Server 10000
```
The server will start listening on port 12345, supporting multiple client connections.

2. Start one or more clients:
```bash
java Client localhost 10000
```
Clients will connect to the server running on localhost at port 12345.

3. Enter messages in the client's console to send to the server:

```bash
Hello, this is a message from the client!
```
The server will broadcast the message to all connected clients.

4. Transmit messages by reading files:

Clients and the server can transmit messages by reading files from the local file system. To do this, specify the file's path as a message. For example:

```bash
-F path/to/file.txt
```
The file contents will be sent as messages.

## Note
* To exit the server or client, you can enter the command `exit` in the console.
