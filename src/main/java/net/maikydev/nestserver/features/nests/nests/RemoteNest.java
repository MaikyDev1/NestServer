package net.maikydev.nestserver.features.nests.nests;

import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.features.nests.Nest;
import net.maikydev.nestserver.features.nests.NestType;
import net.maikydev.nestserver.features.nests.elements.Button;
import net.maikydev.nestserver.features.nests.elements.Data;
import net.maikydev.nestserver.features.nests.elements.Element;
import net.maikydev.nestserver.features.nests.elements.RemoteElement;
import net.maikydev.nestserver.utils.HtmlMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RemoteNest implements Nest {

    private NestType nestType;
    private String id;
    private HtmlMeta htmlMeta;
    private List<RemoteElement> buttons = new ArrayList<>();
    private List<List<String>> display = new ArrayList<>();
    private JsonObject jsonObject = null;

    public static RemoteNest wrapFromConfig(YamlConfig config, String path) {
        RemoteNest basicNest = new RemoteNest();
        basicNest.htmlMeta = new HtmlMeta(
                config.contains(path + ".meta.title") ? config.getString(path + ".meta.title") : "No Title",
                config.contains(path + ".meta.description") ? config.getString(path + ".meta.description") : null,
                config.contains(path + ".meta.icon") ? config.getString(path + ".meta.icon") : "",
                config.contains(path + ".meta.color") ? config.getString(path + ".meta.color") : "#ffffff",
                config.contains(path + ".meta.hide_from_main") ? config.getBoolean(path + ".meta.hide_from_main") : false
        );
        basicNest.id = path.substring(path.lastIndexOf('.') + 1);
        basicNest.nestType = NestType.valueOf(config.getString(path + ".meta.ui_type").toUpperCase(Locale.ROOT));
        for (Object value : ((List) config.getAny(path + ".display"))) {
            if (value instanceof List)
                basicNest.display.add((List<String>) value);
        }
        System.out.println(config.getSubConfiguration(path + ".buttons").keySet());
        for (String key : config.getSubConfiguration(path + ".buttons").keySet()) {
            basicNest.buttons.add(RemoteElement.wrapFromConfig(config, path + ".buttons." + key, key));
        }
        return basicNest;
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
        if (jsonObject == null) {
            jsonObject = JsonObject.newJsonObject();
            jsonObject.addNewField("meta", htmlMeta.toJson().addNewField("ui_type", nestType.name()));
            jsonObject.addNewField("id", id);

            JsonArray dataArray = JsonArray.newJsonArray();
            for (List<String> listS : display) {
                JsonArray array = JsonArray.newJsonArray();
                for (String s : listS) {
                    if (s.equals(" ") || s.isEmpty())
                        array.addValues("");
                    else array.addValues(s);
                }
                dataArray.addValues(array);
            }
            jsonObject.addNewField("display", dataArray);
            JsonObject buttonsObject = JsonObject.newJsonObject();
            buttons.forEach((e) -> buttonsObject.addNewField(e.getAttachedTo(), e.toJsonObject()));
            jsonObject.addNewField("buttons", buttonsObject);
        }
        return jsonObject;
    }
}
