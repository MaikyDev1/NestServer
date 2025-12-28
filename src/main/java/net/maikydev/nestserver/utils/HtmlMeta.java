package net.maikydev.nestserver.utils;

import net.maikydev.duckycore.data.json.objects.JsonObject;

public record HtmlMeta(String name, String description, String icon, String color) {

    public JsonObject toJson() {
        JsonObject o = JsonObject.newJsonObject();
        if (color != null) o.addNewField("color", color);
        if (description != null) o.addNewField("description", description);
        if (name != null) o.addNewField("name", name);
        if (icon != null) o.addNewField("icon", icon);
        return o;
    }

}
