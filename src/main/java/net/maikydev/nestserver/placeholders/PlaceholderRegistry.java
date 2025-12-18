package net.maikydev.nestserver.placeholders;

import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.utils.Pair;

import java.util.HashMap;

public class PlaceholderRegistry {

    final private HashMap<String, String> placeholders = new HashMap<>();

    public void addPlaceholder(String placeholder, String value) {
        placeholders.put(placeholder, value);
    }

    public void removePlaceholder(String placeholder, String value) {
        placeholders.remove(placeholder, value);
    }

    public String apply(String toApply) {
        for (String key : placeholders.keySet()) {
            toApply = toApply.replaceAll("%" + key + "%", placeholders.get(key));
        }
        return toApply;
    }

    public final String applyCustom(String toApply, Pair<String, String> placeholder) {
        return toApply.replaceAll("%" + placeholder.getKey() + "%", placeholders.get(placeholder.getValue()));

    }

}
