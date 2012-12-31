package com.rvc.gui;

import com.rvc.server.ServerSettings;

import javax.swing.*;

public class LabelManager {

    private final ServerSettings serverSettings;
    private final JLabel osName;
    private final JLabel internalIp;
    private final JLabel externalIp;
    private final JLabel macAddress;
    private final JLabel port;
    private final JLabel status;
    private final JLabel error;

    public LabelManager(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
        osName = new JLabel(getOsNameLabel());
        internalIp = new JLabel(getInternalLabel());
        externalIp = new JLabel(getExternalLabel());
        macAddress = new JLabel(getMacAddressLabel());
        port = new JLabel(getPortLabel());
        status = new JLabel("Initialising server...");
        error = new JLabel();
    }

    private String getOsNameLabel() {
        return "OS : " + System.getProperty("os.name");
    }

    private String getInternalLabel() {
        return "Internal Address : " + serverSettings.getInternalIp();
    }

    private String getExternalLabel() {
        return "External Address : " + serverSettings.getExternalIp();
    }

    private String getMacAddressLabel() {
        return "Mac Address : " + serverSettings.getMacAddress();
    }

    private String getPortLabel() {
        return "Port : " + serverSettings.getPort();
    }

    public void addAllLabels(JPanel panel) {
        panel.add(osName);
        panel.add(internalIp);
        panel.add(externalIp);
        panel.add(macAddress);
        panel.add(port);
        panel.add(status);
        panel.add(error);
    }

    public void setStatusText(final String update) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                status.setText(update);
            }
        });
    }

    public void setErrorText(final String update) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                error.setText(update);
            }
        });
    }
}
