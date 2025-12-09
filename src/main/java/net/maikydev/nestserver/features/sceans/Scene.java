package net.maikydev.nestserver.features.sceans;

import lombok.Getter;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.features.devices.Device;
import net.maikydev.nestserver.features.devices.DeviceRegistry;
import net.maikydev.nestserver.utils.HtmlMeta;

import java.util.ArrayList;
import java.util.Locale;

@Getter
public class Scene {

    private String id;
    private SceneType sceneType;
    private boolean currentState;
    private ArrayList<String> enableActions;
    private ArrayList<String> disableActions;
    private HtmlMeta htmlMeta;

    public static Scene wrapNewScene(YamlConfig config, String path) {
        Scene scene = new Scene();
        scene.id = path.substring(path.lastIndexOf('.') + 1);
        scene.htmlMeta = new HtmlMeta(
                config.getString(path + ".title"),
                config.contains(path + ".description") ? config.getString(path + ".description") : "",
                config.getString(path + ".icon")
        );
        scene.sceneType = SceneType.valueOf(config.getString(path + ".type").toUpperCase(Locale.ROOT));
        switch (scene.sceneType) {
            case STATEFUL -> {
                scene.enableActions = config.getStringList(path + ".enable_actions");
                scene.disableActions = config.getStringList(path + ".disable_actions");
                scene.currentState = false;
            }
            case STATELESS -> {
                scene.enableActions = config.getStringList(path + ".actions");
            }
        }
        return scene;
    }

    public void turnState() {
        switch (sceneType) {
            case STATEFUL -> {
                if (currentState) {
                    runTurnOffActions();
                } else {
                    runTurnOnActions();
                }
            }
            case STATELESS -> runTurnOnActions();
        }
    }

    public void runTurnOnActions() {
        enableActions.forEach(cmd -> {
            String[] args = cmd.split(" ");
            Device device = DeviceRegistry.DEVICE.getDevice(args[1]);
            if (device != null)
                device.runAction(args[2], null);
        });
        currentState = true;
    }

    public void runTurnOffActions() {
        disableActions.forEach(cmd -> {
            String[] args = cmd.split(" ");
            Device device = DeviceRegistry.DEVICE.getDevice(args[1]);
            if (device != null)
                device.runAction(args[2], null);
        });
        currentState = false;
    }

    public enum SceneType {
        STATEFUL,
        STATELESS,
    }

}
