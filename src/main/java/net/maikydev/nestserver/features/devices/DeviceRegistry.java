package net.maikydev.nestserver.features.devices;

import lombok.Getter;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.devices.runner.RunnerRegistry;

import java.util.HashMap;

public enum DeviceRegistry {
    DEVICE;

    @Getter
    private final HashMap<String, Device> devices = new HashMap<>();
    @Getter
    private final RunnerRegistry runnersRegistry = new RunnerRegistry();

    public void addDevices(YamlConfig config, String path) {
        config.getSubConfiguration(path).keySet().forEach(key -> {
            devices.put(key, Device.wrapNewDevice(config, path + "." + key));
        });
    }

    public void addDevice(Device device) {
        devices.put(device.getId(), device);
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
