package com.rvc.gui.main;

import com.rvc.ServerController;
import com.rvc.gui.tray.TrayExitCallback;
import com.rvc.server.ServerCallbacks;
import com.rvc.server.ServerSettings;

import javax.swing.*;

public class ServerGui implements ServerCallbacks, TrayExitCallback {

    private final GuiCreator guiCreator;
    private final ServerController serverController;

    public ServerGui(ServerSettings serverSettings, ServerController serverController) {
        guiCreator = new GuiCreator(new LabelManager(serverSettings), this, serverController);
        this.serverController = serverController;
        createGui();
    }

    private void createGui() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                guiCreator.create();
            }
        });
    }

    private void serverExit() {
        System.out.println("Server Quitting");
        serverController.stopServer();
    }

    @Override
    public void onStatusUpdate(String update) {
        guiCreator.updateStatus(update);
    }

    @Override
    public void onErrorUpdate(String update) {
        guiCreator.updateError(update);
    }

    @Override
    public void onClientConnected() {
        guiCreator.showClientConnectedPopup();
    }

    @Override
    public void onClientDisconnected() {
        guiCreator.showClientDisconnectedPopup();
    }

    @Override
    public void onTrayExit() {
        serverExit();
    }

}
