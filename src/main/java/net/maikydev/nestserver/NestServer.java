package net.maikydev.nestserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.features.devices.DeviceRegistry;
import net.maikydev.nestserver.features.devices.runner.RunnerRegistry;
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
    private HttpServer server;

    public void onStart() {
        this.config = YamlConfig.fromFileName("config.yml");
        RunnerRegistry.FACTORY.loadDefaults();
        DeviceRegistry.DEVICE.loadDevices();
        SceneRegistry.SCENE.loadScenes();
        startWebServer();
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
