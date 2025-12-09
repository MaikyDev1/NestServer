package net.maikydev.nestserver.features.sceans;

import lombok.Getter;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.devices.Device;

import java.util.HashMap;

public enum SceneRegistry {
    SCENE;

    @Getter
    private final HashMap<String, Scene> scenes = new HashMap<>();

    public void loadScenes() {
        NestServer.SERVER.getConfig().getSubConfiguration("scenes").keySet().forEach(key -> {
            scenes.put(key, Scene.wrapNewScene(NestServer.SERVER.getConfig(), "scenes." + key));
        });
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
