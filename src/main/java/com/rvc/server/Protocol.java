package com.rvc.server;

import com.rvc.util.Shutdown;
import com.rvc.volume.VolumeController;
import com.rvc.volume.controller.VolumeControllerFactory;

class Protocol {

    private static final String UPDATE_VOLUME = "uvol";
    private static final String CONNECTED = "conn";
    private static final String SHUTDOWN = "sdown";
    private static final String EXIT = "exit";
    private static final String VOLUME = "vol";

    private final ConnectionState server;
    private final VolumeController volumeController;

    private String exitCode;
	private String vol;

	Protocol(ConnectionState server) {
        this.server = server;
        exitCode = "0";
		this.vol = "00";
        volumeController = VolumeControllerFactory.getVolumeController();
	}

    public String processInput(String input) {
        if (input.equals(CONNECTED)) {
        	server.setClientConnected(true);
        }
    	
    	if (server.isClientConnected()) {
            handleMessage(input);
    	}
            
        return exitCode + getServerState() + vol;
    }

    private void handleMessage(String input) {
        if (input.equals(UPDATE_VOLUME)) {
            vol = String.format("%02x", volumeController.getVolume());
        }
        if (input.equals(SHUTDOWN)) {
            System.out.println("Client chose shutdown");
            new Shutdown().shutDown();
        }
        if (input.equals(EXIT)) {
            server.setClientConnected(false);
            exitCode = "1";
            System.out.println("Client chose to disconnect");
        }
        if (input.substring(0, 3).equals(VOLUME)) {
            volumeController.setVolume(Integer.parseInt(input.substring(3)));
            System.out.println(input.substring(3));
        }
    }

    private String getServerState() {
        return String.valueOf(server.isServerRunning()? 1:0);
    }

}
	