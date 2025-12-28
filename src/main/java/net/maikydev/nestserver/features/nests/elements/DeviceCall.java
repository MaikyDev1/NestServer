package net.maikydev.nestserver.features.nests.elements;

import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;

import java.util.HashMap;
import java.util.Map;

public record DeviceCall (String device, String action, String read_parameter, Map<String, String> params) {

    public static DeviceCall wrapFromConfig(YamlConfig config, String path) {
        Map<String, String> params;
        if (config.contains(path + ".params")) {
            params = new HashMap<>();
            config.getSubConfiguration(path + ".params").forEach((k, v) -> params.put(k, String.valueOf(v)));
        } else params = null;
        return new DeviceCall(
                config.contains(path + ".device") ? config.getString(path + ".device") : "nan",
                config.contains(path + ".action") ? config.getString(path + ".action") : "nan",
                config.contains(path + ".read_parameter") ? config.getString(path + ".read_parameter") : null,
                params
        );
    }

    public JsonObject toJson() {
        JsonObject o = JsonObject.newJsonObject();
        if (device != null) o.addNewField("device", device);
        if (action != null) o.addNewField("action", action);
        if (read_parameter != null) o.addNewField("read_parameter", read_parameter);
        if (params != null) {
            JsonObject paramsJson = JsonObject.newJsonObject();
            params.forEach(paramsJson::addNewField);
            o.addNewField("params", paramsJson);
        }
        return o;
    }

}
