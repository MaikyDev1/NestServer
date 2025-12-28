package net.maikydev.nestserver.features.nests.elements;

import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.utils.HtmlMeta;

public class Data implements Element {

    private final String id;
    private final HtmlMeta meta;
    private final String dataType;
    private final DeviceCall getStateDevice;

    public Data(String id, HtmlMeta meta, String type, DeviceCall getStateDevice) {
        this.id = id;
        this.meta = meta;
        this.dataType = type;
        this.getStateDevice = getStateDevice;
    }

    public static Data wrapFromConfig(YamlConfig config, String path) {
        HtmlMeta meta = new HtmlMeta(
                config.contains(path + ".meta.title") ? config.getString(path + ".meta.title") : "No Title",
                config.contains(path + ".meta.description") ? config.getString(path + ".meta.description") : null,
                config.contains(path + ".meta.icon") ? config.getString(path + ".meta.icon") : "tabler:file-unknown",
                config.contains(path + ".meta.color") ? config.getString(path + ".meta.color") : "#7315d1"
        );
        DeviceCall getStateDevice = DeviceCall.wrapFromConfig(config, path + ".value");
        return new Data(
                config.getString(path + ".id"), meta,
                config.getString(path + ".meta.data_type"), getStateDevice
        );
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject jo = JsonObject.newJsonObject();
        jo.addNewField("id", id).addNewField("meta", meta.toJson().addNewField("data_type", dataType))
                .addNewField("value", getStateDevice.toJson());
        return jo;
    }

}
