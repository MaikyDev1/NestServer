package net.maikydev.nestserver.features.devices;

import lombok.Getter;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.exceptions.ActionHandleException;
import net.maikydev.nestserver.features.devices.runner.Runner;

import java.util.HashMap;

@Getter
public class Device {

    private String id;
    private final HashMap<String, Runner> actions = new HashMap<>();

    public static Device wrapNewDevice(YamlConfig config, String path) {
        Device device = new Device();
        device.id = path.substring(path.lastIndexOf('.') + 1);
        config.getSubConfiguration(path + ".actions").keySet().forEach(key ->
                device.actions.put(key, DeviceRegistry.DEVICE.getRunnersRegistry().createRunner(config, path + ".actions." + key))
        );
        return device;
    }

    public boolean hasAction(String action) {
        return actions.containsKey(action);
    }

    public JsonObject runAction(String action, HashMap<String, String> placeholders) {
        if (!hasAction(action))
            return JsonObject.newJsonObject().addNewField("error", "No action by this name!");
        try {
            return actions.get(action).handle(placeholders);
        } catch (ActionHandleException e) {
            return JsonObject.newJsonObject().addNewField("error", e.getMessage());
        }
    }

}
