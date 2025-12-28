package net.maikydev.nestserver.features.nests.elements;

import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.utils.HtmlMeta;
import net.maikydev.nestserver.utils.Pair;

public class Button implements Element {

    private final String id;
    private final HtmlMeta meta;
    private final String type;
    private final DeviceCall getStateDevice;
    private final DeviceCall getEnableDevice;
    private final DeviceCall getDisableDevice;

    public Button(String id, HtmlMeta meta, String type, DeviceCall getStateDevice, DeviceCall getEnableDevice, DeviceCall getDisableDevice) {
        this.id = id;
        this.meta = meta;
        this.type = type;
        this.getStateDevice = getStateDevice;
        this.getEnableDevice = getEnableDevice;
        this.getDisableDevice = getDisableDevice;
    }

    public static Button wrapFromConfig(YamlConfig config, String path) {
        HtmlMeta meta = new HtmlMeta(
                config.contains(path + ".meta.title") ? config.getString(path + ".meta.title") : "No Title",
                config.contains(path + ".meta.description") ? config.getString(path + ".meta.description") : null,
                config.contains(path + ".meta.icon") ? config.getString(path + ".meta.icon") : "carbon:unknown-filled",
                config.contains(path + ".meta.color") ? config.getString(path + ".meta.color") : "#ffffff"
        );
        DeviceCall getStateDevice = null;
        DeviceCall getDisableDevice = null;
        if (config.getString(path + ".meta.type").equalsIgnoreCase("state")) {
            getStateDevice = DeviceCall.wrapFromConfig(config, path + ".get_state");
            getDisableDevice = DeviceCall.wrapFromConfig(config, path + ".disable_actions");
        }
        DeviceCall getEnableDevice = DeviceCall.wrapFromConfig(config, path + ".enable_actions");
        return new Button(
                config.getString(path + ".id"), meta,
                config.getString(path + ".meta.type"), getStateDevice, getEnableDevice, getDisableDevice
        );
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject jo = JsonObject.newJsonObject();
        jo.addNewField("id", id).addNewField("meta", meta.toJson().addNewField("type", type))
                .addNewField("enable_actions", getEnableDevice.toJson());
        if (type.equalsIgnoreCase("state")) {
            jo.addNewField("disable_actions", getDisableDevice.toJson()).addNewField("get_state", getStateDevice.toJson());
        }
        return jo;
    }

}
