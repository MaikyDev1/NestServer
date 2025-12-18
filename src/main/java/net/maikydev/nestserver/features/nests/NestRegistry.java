package net.maikydev.nestserver.features.nests;

import lombok.Getter;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.devices.Device;

import java.util.HashMap;

public enum NestRegistry {
    NESTS;

    @Getter
    private final HashMap<String, Nest> nests = new HashMap<>();

    public void loadDevices() {
        NestServer.SERVER.getNestsConfig().getSubConfiguration("devices").keySet().forEach(key -> {
            devices.put(key, Device.wrapNewDevice(NestServer.SERVER.getConfig(), "devices." + key));
        });
    }

    public Device getDevice(String key) {
        if (deviceExists(key))
            return devices.get(key);
        return null;
    }

    public boolean deviceExists(String device) {
        return devices.containsKey(device);
    }

}
