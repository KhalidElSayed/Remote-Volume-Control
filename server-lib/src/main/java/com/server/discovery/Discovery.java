package com.server.discovery;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Discovery {

    private final JmDNS mdnsServer;
    private final DiscoverySettings settings;
    private final Callback callback;

    public interface Callback {
        void onDiscoveryUpdate(String update);
    }

    public Discovery(DiscoverySettings discoverySettings, Callback callback) throws IOException {
        this.callback = callback;
        mdnsServer = JmDNS.create(discoverySettings.getInternalAddress());
        settings = discoverySettings;
    }

    public void start(final Failed failureCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServiceInfo testService = createService();
                try {
                    registerService(testService);
                } catch (IOException e) {
                    e.printStackTrace();
                    failureCallback.onDiscoveryFailed();
                }
            }
        }).start();

    }

    ServiceInfo createService() {
        return ServiceInfo.create(settings.getServiceType(), createServiceName(), settings.getPort(), "RVC");
    }

    private String createServiceName() {
        try {
            return "RVC " + InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't retrieve host name");
        }
    }

    private void registerService(ServiceInfo testService) throws IOException {
        mdnsServer.registerService(testService);
        callback.onDiscoveryUpdate("Discovery ready");
    }

    public void finish() {
        mdnsServer.unregisterAllServices();
    }

    public interface Failed {
        void onDiscoveryFailed();
    }

}
