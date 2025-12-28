package net.maikydev.nestserver.features.nests;

import lombok.Getter;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.devices.Device;
import net.maikydev.nestserver.features.nests.nests.BasicNest;
import net.maikydev.nestserver.features.sceans.Scene;
import net.maikydev.nestserver.features.sceans.SceneRegistry;

import java.util.HashMap;

public class NestRegistry {

    @Getter
    private final HashMap<String, Nest> nests = new HashMap<>();

    public NestRegistry addNests(YamlConfig config, String path) {
        config.getSubConfiguration(path).keySet().forEach(key -> {
            Nest nest = switch (config.getString(path + "." + key + ".meta.ui_type").toUpperCase()) {
                case "BASIC_NEST" -> BasicNest.wrapFromConfig(config, path + "." + key);
                default -> null;
            };
            if (nest != null)
                nests.put(key, nest);
        });
        return this;
    }

    public NestRegistry addScene(Nest scene) {

        return this;
    }

    public Nest getNest(String key) {
        if (nestExists(key))
            return nests.get(key);
        return null;
    }

    public boolean nestExists(String nest) {
        return nests.containsKey(nest);
    }

}
