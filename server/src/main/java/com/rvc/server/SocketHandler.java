package com.rvc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketHandler {

    private static final String UPDATE_WAITING_FOR_CLIENT = "Waiting for client";
    private static final String UPDATE_CLIENT_SOCKET_CONNECTED = "Client connected";
    private static final String ERROR_SERVER_SOCKET_IO_EXCEPTION = "couldn't connect : IOException || is the server already running?";

    private final ServerCallbacks callback;

    private Socket clientSocket;
    private ServerSocket serverSocket;
    private ConnectionState connectionState;

    public SocketHandler(ServerCallbacks callback, ConnectionState connectionState) {
        this.callback = callback;
        this.connectionState = connectionState;
    }

    public void createSockets(int port) throws IOException {
        serverSocket = createServerSocket(port);
        clientSocket = createClientSocket();
    }

    private ServerSocket createServerSocket(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(ERROR_SERVER_SOCKET_IO_EXCEPTION);
            System.exit(1);
        }
        return serverSocket;
    }

    private Socket createClientSocket() throws IOException {
        callback.onStatusUpdate(UPDATE_WAITING_FOR_CLIENT);
        clientSocket = serverSocket.accept();
        callback.onStatusUpdate(UPDATE_CLIENT_SOCKET_CONNECTED);
        connectionState.setServerRunning(true);
        return clientSocket;
    }

    public void closeSockets() throws IOException {
        if (serverSocket != null && clientSocket != null) {
            serverSocket.close();
            clientSocket.close();
            System.out.println("Sockets closed");
        } else {
            serverSocket.close();
        }
    }

    public Socket clientSocket() {
        return clientSocket;
    }
}
