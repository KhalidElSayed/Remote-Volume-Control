package com.server.util;

import java.io.IOException;

public class Shutdown {

    private final OSHelper osHelper;

    public Shutdown() {
        osHelper = new OSHelper();
    }

    public void shutDown() {
        if (osHelper.isWindows7()) {
            shutdownWindows();
        } else {
            shutdownLinux();
        }
    }

    private void shutdownLinux() {
        serverCommand("/sbin/halt");
    }

    private void shutdownWindows() {
        serverCommand(new String[] { "shutdown", "-s"});
    }

    private void serverCommand(String cmd) {
        this.serverCommand(new String[] { cmd });
    }

    private void serverCommand(final String[] cmd) {

        Thread t = new Thread() {
            public void run() {
                try {
                    @SuppressWarnings("unused")
                    Process process = Runtime.getRuntime().exec(cmd);
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        };
        t.start();
    }

}
