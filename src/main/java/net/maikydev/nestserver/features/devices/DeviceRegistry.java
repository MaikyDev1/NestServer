package net.maikydev.nestserver.features.devices;

import lombok.Getter;
import net.maikydev.nestserver.NestServer;

import java.util.HashMap;

public enum DeviceRegistry {
    DEVICE;

    @Getter
    private final HashMap<String, Device> devices = new HashMap<>();

    public void loadDevices() {
        NestServer.SERVER.getConfig().getSubConfiguration("devices").keySet().forEach(key -> {
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
