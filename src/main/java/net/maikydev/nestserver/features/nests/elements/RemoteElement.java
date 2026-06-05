package net.maikydev.nestserver.features.nests.elements;

import lombok.Getter;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;

import java.util.HashMap;

@Getter
public class RemoteElement implements Element {
    private String attachedTo;
    private String type;
    private String text = "";
    private HashMap<String, DeviceCall> actionsList;

    public static RemoteElement wrapFromConfig(YamlConfig config, String path, String attachedTo) {
        RemoteElement element = new RemoteElement();
        element.actionsList = new HashMap<>();
        element.attachedTo = attachedTo;
        element.type = config.getString(path + ".type");
        if (config.contains(path + ".text"))
            element.text = config.getString(path + ".text");

        if (config.contains(path + ".action")) {
            element.actionsList.put("default", DeviceCall.wrapFromConfig(config, path + ".action"));
        } else if (config.contains(path + ".actions")) {
            for (String key : config.getSubConfiguration(path + ".actions").keySet()) {
                element.actionsList.put(key, DeviceCall.wrapFromConfig(config, path + ".actions." + key));
            }
        }
        return element;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject jo = JsonObject.newJsonObject();
        jo.addNewField("type", type);
        if (text != null && text != "") jo.addNewField("text", type);
        if (actionsList.size() == 1) {
            jo.addNewField("action", actionsList.get("default").toJson());
        } else {
            JsonObject continueJo = JsonObject.newJsonObject();
            actionsList.forEach((key, value) -> continueJo.addNewField(key, value.toJson()));
            jo.addNewField("actions", continueJo);
        }
        return jo;
    }
}
