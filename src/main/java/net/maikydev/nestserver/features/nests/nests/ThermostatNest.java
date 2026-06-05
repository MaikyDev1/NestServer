package net.maikydev.nestserver.features.nests.nests;

import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.features.nests.Nest;
import net.maikydev.nestserver.features.nests.NestType;
import net.maikydev.nestserver.features.nests.elements.Button;
import net.maikydev.nestserver.features.nests.elements.Data;
import net.maikydev.nestserver.features.nests.elements.DeviceCall;
import net.maikydev.nestserver.features.nests.elements.Element;
import net.maikydev.nestserver.utils.HtmlMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ThermostatNest implements Nest {

    private NestType nestType;
    private String id;
    private DeviceCall getState;
    private DeviceCall turnOn;
    private DeviceCall turnOff;
    private DeviceCall getTime;
    private DeviceCall addTime;
    private DeviceCall removeTime;
    private HtmlMeta htmlMeta;
    private ThermostatConfiguration thermostatConfiguration;

    public static ThermostatNest wrapFromConfig(YamlConfig config, String path) {
        ThermostatNest thermostatNest = new ThermostatNest();
        thermostatNest.htmlMeta = new HtmlMeta(
                config.contains(path + ".meta.title") ? config.getString(path + ".meta.title") : "No Title",
                config.contains(path + ".meta.description") ? config.getString(path + ".meta.description") : null,
                config.contains(path + ".meta.icon") ? config.getString(path + ".meta.icon") : "",
                config.contains(path + ".meta.color") ? config.getString(path + ".meta.color") : "#ffffff",
                config.contains(path + ".meta.hide_from_main") ? config.getBoolean(path + ".meta.hide_from_main") : false
        );
        thermostatNest.id = path.substring(path.lastIndexOf('.') + 1);
        thermostatNest.thermostatConfiguration = ThermostatConfiguration.wrapFromConfig(config, path + ".configuration");
        thermostatNest.nestType = NestType.valueOf(config.getString(path + ".meta.ui_type").toUpperCase(Locale.ROOT));
        thermostatNest.getState = DeviceCall.wrapFromConfig(config, path + ".get_state");
        thermostatNest.getTime = DeviceCall.wrapFromConfig(config, path + ".get_time");
        thermostatNest.turnOn = DeviceCall.wrapFromConfig(config, path + ".turn_on");
        thermostatNest.turnOff = DeviceCall.wrapFromConfig(config, path + ".turn_off");
        thermostatNest.addTime = DeviceCall.wrapFromConfig(config, path + ".add_time");
        thermostatNest.removeTime = DeviceCall.wrapFromConfig(config, path + ".remove_time");
        return thermostatNest;
    }

    @Override
    public JsonObject toHeaderJsonObject() {
        JsonObject object = JsonObject.newJsonObject();
        object.addNewField("id", id);
        object.addNewField("meta", htmlMeta.toJson().addNewField("ui_type", nestType.name()));
        return object;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject object = JsonObject.newJsonObject();
        object.addNewField("meta", htmlMeta.toJson().addNewField("ui_type", nestType.name()));
        object.addNewField("id", id);
        object.addNewField("configuration", thermostatConfiguration.toJson());
        object.addNewField("get_state", getState.toJson());
        object.addNewField("get_time", getTime.toJson());
        object.addNewField("turn_on", turnOn.toJson());
        object.addNewField("turn_off", turnOff.toJson());
        object.addNewField("add_time", addTime.toJson());
        object.addNewField("remove_time", removeTime.toJson());
        return object;
    }
}
