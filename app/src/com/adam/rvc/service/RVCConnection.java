package com.adam.rvc.service;

import com.adam.rvc.util.StatusUpdater;

import java.io.IOException;

public class RVCConnection implements ServerConnection {

    private final RVCClient client;
    private final StatusUpdater statusUpdater;

    public RVCConnection(String ip, int port, RVCClient.OnMessageReceived onMessageReceived, StatusUpdater statusUpdater) throws IOException {
        this.statusUpdater = statusUpdater;
        client = new RVCClient(ip, port);
        client.setMessageListener(onMessageReceived);
    }

    @Override
    public void connect() {
        statusUpdater.updateStatusAndLog("Connecting...");
        client.connect();
    }

    @Override
    public void write(String message) {
        client.writeToServer(message);
    }

    @Override
    public void disconnect() {
        statusUpdater.updateStatus("Disconnected");
        client.disconnect();
    }

}
