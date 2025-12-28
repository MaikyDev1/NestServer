package net.maikydev.nestserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import net.maikydev.duckycore.data.json.DuckyJson;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.features.access.AccessController;
import net.maikydev.nestserver.features.devices.DeviceRegistry;
import net.maikydev.nestserver.features.nests.NestRegistry;
import net.maikydev.nestserver.features.nests.elements.Button;
import net.maikydev.nestserver.features.sceans.Scene;
import net.maikydev.nestserver.features.sceans.SceneRegistry;
import net.maikydev.nestserver.routes.device.DeviceHandler;
import net.maikydev.nestserver.routes.scene.SceneHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;

@Getter
public enum NestServer {
    SERVER;

    private YamlConfig config;
    private YamlConfig devicesConfig;
    private YamlConfig nestsConfig;
    private HttpServer server;

    private AccessController accessController;
    private SceneRegistry sceneRegistry;
    private NestRegistry nestRegistry;

    public void onStart() {
        this.config = YamlConfig.fromFileName("config.yml");
        this.devicesConfig = YamlConfig.fromFileName("devices.yml");
        this.nestsConfig = YamlConfig.fromFileName("nests.yml");
        loadConfigs();
        startWebServer();
    }

    private void loadConfigs() {
        accessController = AccessController.wrapFromConfig(config);
        //DeviceRegistry.DEVICE.loadDevices();
        //sceneRegistry = new SceneRegistry().addScenes(nestsConfig, "scenes");
        nestRegistry = new NestRegistry().addNests(nestsConfig, "nests");
        System.out.println(DuckyJson.deserialization(nestRegistry.getNest("balcony").toJsonObject()));
    }

    private void startWebServer() {
        HashMap<String, HttpHandler> handlers = new HashMap<>();
        handlers.put("/api/v1/device", new DeviceHandler());
        handlers.put("/api/v1/scene", new SceneHandler());

        try {
            this.server = HttpServer.create(new InetSocketAddress(8000), 0);
            handlers.forEach((path, handler) -> this.server.createContext(path, handler));
            this.server.setExecutor(Executors.newFixedThreadPool(10));
            this.server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
