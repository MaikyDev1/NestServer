package net.maikydev.nestserver.routes.scene;

import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.ducket.exchange.DuckletResponse;
import net.maikydev.nestserver.ducket.annotations.RequestMapping;
import net.maikydev.nestserver.ducket.annotations.RequestUrlParam;
import net.maikydev.nestserver.ducket.annotations.http_types.GetRequest;
import net.maikydev.nestserver.features.sceans.Scene;

@RequestMapping("/api/v1/scene")
public class SceneRoute {

    @GetRequest
    @RequestMapping("/all")
    public DuckletResponse getAllScene() {
        JsonArray array = JsonArray.newJsonArray();
        NestServer.SERVER.getSceneRegistry().getScenes().values().forEach((scene) -> array.addValues(this.getSceneDetails(scene)));
        return DuckletResponse.ok().sendJson(array);
    }

    @GetRequest
    @RequestMapping("/[scene]")
    public DuckletResponse getAllScene(@RequestUrlParam("scene") String sceneKey) {
        Scene scene = NestServer.SERVER.getSceneRegistry().getScene(sceneKey);
        if (scene == null)
            return DuckletResponse.notFound().sendJson("error", "Scene not found!");
        return DuckletResponse.ok().sendJson(this.getSceneDetails(scene));
    }

    @GetRequest
    @RequestMapping("/[scene]/turn/[state]")
    public DuckletResponse getTrunASceneToAState(@RequestUrlParam("scene") String sceneKey, @RequestUrlParam("state") String state) {
        Scene scene = NestServer.SERVER.getSceneRegistry().getScene(sceneKey);
        if (scene == null)
            return DuckletResponse.notFound().sendJson("error", "Scene not found!");
        boolean newState = state.equalsIgnoreCase("true") || state.equalsIgnoreCase("1") || state.equalsIgnoreCase("on");
        switch (scene.getSceneType()) {
            case STATELESS -> scene.turnState();
            case STATEFUL -> {
                if (newState)
                    scene.runTurnOffActions();
                else
                    scene.runTurnOnActions();
            }
        }
        return DuckletResponse.ok().sendJson(this.getSceneDetails(scene));
    }

    @GetRequest
    @RequestMapping("/[scene]/turn")
    public DuckletResponse turnAState(@RequestUrlParam("scene") String sceneKey) {
        Scene scene = NestServer.SERVER.getSceneRegistry().getScene(sceneKey);
        if (scene == null)
            return DuckletResponse.notFound().sendJson("error", "Scene not found!");
        scene.turnState();
        return DuckletResponse.ok().sendJson(this.getSceneDetails(scene));
    }

    private JsonObject getSceneDetails(Scene scene) {
        return JsonObject.newJsonObject()
                .addNewField("id", scene.getId())
                .addNewField("scene_type", String.valueOf(scene.getSceneType()))
                .addNewField("current_state", scene.isCurrentState())
                .addNewField("name", scene.getHtmlMeta().title())
                .addNewField("description", scene.getHtmlMeta().description())
                .addNewField("icon", scene.getHtmlMeta().icon());
    }

}
