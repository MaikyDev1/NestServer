package net.maikydev.nestserver.routes.scene;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.nestserver.features.devices.Device;
import net.maikydev.nestserver.features.devices.DeviceRegistry;
import net.maikydev.nestserver.features.sceans.Scene;
import net.maikydev.nestserver.features.sceans.SceneRegistry;
import net.maikydev.nestserver.utils.HttpUtils;

import java.io.IOException;
import java.util.Arrays;

public class SceneHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String urlPath = exchange.getRequestURI().getPath();
        if (urlPath.equalsIgnoreCase("/api/v1/scene")) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "No API route by this name!"));
            return;
        }
        String[] url = exchange.getRequestURI().getPath().split("/api/v1/scene/")[1].split("/");

        try {
            if (url.length == 1) {
                this.handleGetScenes(exchange, url[0]);
                return;
            }

            if (url.length == 2 && url[1].equals("run")) {
                this.handleTurnScene(exchange, url[0], null);
                return;
            }
        } catch (Exception e) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "While running we got: " + e.getMessage()));
            e.printStackTrace();
            return;
        }

        HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "No API route by this name!"));
    }

    private void handleGetScenes(HttpExchange exchange, String key) throws IOException {
        if (key.equalsIgnoreCase("all")) {
            JsonArray array = JsonArray.newJsonArray();
            SceneRegistry.SCENE.getScenes().values().forEach((scene) -> array.addValues(this.getSceneDetails(scene)));
            HttpUtils.respondWithJson(exchange, 200, array);
            return;
        }

        Scene scene = SceneRegistry.SCENE.getScene(key);
        if (scene != null) {
            HttpUtils.respondWithJson(exchange, 200, this.getSceneDetails(scene));
            return;
        }
        HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "The scene was not found!"));
    }

    private JsonObject getSceneDetails(Scene scene) {
        return JsonObject.newJsonObject()
                .addNewField("id", scene.getId())
                .addNewField("scene_type", scene.getSceneType())
                .addNewField("current_state", scene.isCurrentState())
                .addNewField("name", scene.getHtmlMeta().name())
                .addNewField("description", scene.getHtmlMeta().description())
                .addNewField("icon", scene.getHtmlMeta().icon());
    }

    private void handleTurnScene(HttpExchange exchange, String sceneKey, Boolean newState) throws IOException {
        Scene scene = SceneRegistry.SCENE.getScene(sceneKey);
        if (scene == null) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "The Scene was not found!"));
            return;
        }
        switch (scene.getSceneType()) {
            case STATELESS -> scene.turnState();
            case STATEFUL -> {
                if (newState == null) {
                    scene.turnState();
                } else {
                    if (scene.isCurrentState())
                        scene.runTurnOffActions();
                    else
                        scene.runTurnOnActions();
                }
            }
        }
        HttpUtils.respondWithJson(exchange, 200, JsonObject.newJsonObject().addNewField("state", scene.isCurrentState()));
    }

}
