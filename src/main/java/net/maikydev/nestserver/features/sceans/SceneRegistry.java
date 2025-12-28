package net.maikydev.nestserver.features.sceans;

import lombok.Getter;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.devices.Device;

import java.util.Arrays;
import java.util.HashMap;

public class SceneRegistry {

    @Getter
    private final HashMap<String, Scene> scenes = new HashMap<>();

    public SceneRegistry addScenes(YamlConfig config, String path) {
        config.getSubConfiguration(path).keySet().forEach(key -> {
            scenes.put(key, Scene.wrapNewScene(NestServer.SERVER.getConfig(), "scenes." + key));
        });
        return this;
    }

    public SceneRegistry addScene(Scene scene) {
        scenes.put(scene.getId(), scene);
        return this;
    }

    public Scene getScene(String key) {
        if (sceneExists(key))
            return scenes.get(key);
        return null;
    }

    public boolean sceneExists(String device) {
        return scenes.containsKey(device);
    }

}
