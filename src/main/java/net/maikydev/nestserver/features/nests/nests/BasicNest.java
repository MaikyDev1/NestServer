package net.maikydev.nestserver.features.nests.nests;

import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.features.nests.Nest;
import net.maikydev.nestserver.features.nests.NestType;
import net.maikydev.nestserver.features.nests.elements.Button;
import net.maikydev.nestserver.features.nests.elements.Data;
import net.maikydev.nestserver.features.nests.elements.Element;
import net.maikydev.nestserver.utils.HtmlMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BasicNest implements Nest {

    private NestType nestType;
    private final List<Element> buttons = new ArrayList<>();
    private final List<Element> data = new ArrayList<>();
    private HtmlMeta htmlMeta;

    public static BasicNest wrapFromConfig(YamlConfig config, String path) {
        BasicNest basicNest = new BasicNest();
        basicNest.htmlMeta = new HtmlMeta(
                config.contains(path + ".meta.title") ? config.getString(path + ".meta.title") : "No Title",
                config.contains(path + ".meta.description") ? config.getString(path + ".meta.description") : null,
                null,
                config.contains(path + ".meta.color") ? config.getString(path + ".meta.color") : "#ffffff"
        );
        basicNest.nestType = NestType.valueOf(config.getString(path + ".meta.ui_type").toUpperCase(Locale.ROOT));
        config.getSubConfiguration(path + ".data").keySet().forEach(k -> basicNest.data.add(Data.wrapFromConfig(config, path + ".data." + k)));
        config.getSubConfiguration(path + ".buttons").keySet().forEach(k -> basicNest.buttons.add(Button.wrapFromConfig(config, path + ".buttons." + k)));
        return basicNest;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject object = JsonObject.newJsonObject();
        object.addNewField("meta", htmlMeta.toJson().addNewField("ui_type", nestType.name()));

        JsonArray dataArray = JsonArray.newJsonArray();
        data.forEach((e) -> dataArray.addValues(e.toJsonObject()));
        object.addNewField("data", dataArray);

        JsonArray buttonArray = JsonArray.newJsonArray();
        buttons.forEach((e) -> buttonArray.addValues(e.toJsonObject()));
        object.addNewField("button", buttonArray);
        return object;
    }
}
