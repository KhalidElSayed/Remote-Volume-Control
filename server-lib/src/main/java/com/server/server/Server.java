package com.server.server;

import com.server.Discovery;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Server implements ConnectionHandler.Callback, Discovery.Callback {

    private static final String UPDATE_SERVER_START = "Server starting";

    private final ServerState serverState;
    private final List<ConnectionHandler> readingThreads;
    private final Discovery discovery;

    private SocketHandler socketHandler;
    private ClientConnectionCallback connectionCallback;
    private final ServerSettings settings;
    private ClientMessageCallback clientMessageCallback;

    public Server(ServerSettings serverSettings) throws IOException {
        settings = serverSettings;
        readingThreads = new ArrayList<ConnectionHandler>();
        discovery = new Discovery(settings, this);
        serverState = new ServerState();
    }

    public void setConnectionCallback(ClientConnectionCallback callback) {
        this.connectionCallback = callback;
    }

    public void setMessageCallback(ClientMessageCallback callback) {
        this.clientMessageCallback = callback;
    }

    public void startServer() throws IOException {
        updateStatus(UPDATE_SERVER_START);
        discovery.start();
        handleConnections();
    }

    private void updateStatus(String status) {
        connectionCallback.onStatusUpdate(status);
    }

    private void handleConnections() throws IOException {
        initConnection();
        clientConnectionLoop();
        closeServerConnection();
    }

    private void initConnection() throws IOException {
        initServerSocket();
        serverState.setServerRunning(true);
    }

    private void initServerSocket() throws IOException {
        socketHandler = new SocketHandler(connectionCallback);
        socketHandler.initServerSocket(settings.getPort());
    }

    private void clientConnectionLoop() throws IOException {
        while (serverState.isServerRunning()) {
            Socket client = waitForNewClient();
            startNewConnectionThread(client);
        }
    }

    private Socket waitForNewClient() throws IOException {
        return socketHandler.waitForClient();
    }

    private void startNewConnectionThread(final Socket clientSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handleNewConnection(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleNewConnection(Socket clientSocket) throws IOException {
        ConnectionHandler connectionHandler = new ConnectionHandler(this, serverState);
        connectionHandler.handleNewConnection(clientSocket);
    }

    private void closeServerConnection() {
        try {
            socketHandler.closeServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void quit() throws IOException {
        discovery.finish();
        serverState.setServerRunning(false);
        for (ConnectionHandler connectionHandler : readingThreads) {
            connectionHandler.stop();
        }
    }

    @Override
    public void clientThreadEstablished(ConnectionHandler connectionHandler) {
        readingThreads.add(connectionHandler);
        connectionCallback.onClientConnected(connectionHandler.getClientName());
    }

    @Override
    public void clientThreadFinished(ConnectionHandler connectionHandler) {
        readingThreads.remove(connectionHandler);
        connectionCallback.onClientDisconnected(connectionHandler.getClientName());
    }

    @Override
    public void clientMessageReceived(String clientName, String message) {
        clientMessageCallback.onMessageReceived(clientName, message);
        printResponse();
    }

    private void printResponse() {
        readingThreads.get(0).writeToClient(clientMessageCallback.writeToClient());
    }

    @Override
    public void onDiscoveryUpdate(String update) {
        connectionCallback.onDiscoveryStatusUpdate(update);
    }

}
