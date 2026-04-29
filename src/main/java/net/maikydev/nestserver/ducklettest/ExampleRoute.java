package net.maikydev.nestserver.ducklettest;

import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonEntity;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.json.objects.JsonValue;
import net.maikydev.nestserver.ducket.exchange.DuckletRequest;
import net.maikydev.nestserver.ducket.exchange.DuckletResponse;
import net.maikydev.nestserver.ducket.annotations.RequestBody;
import net.maikydev.nestserver.ducket.annotations.auth.RequireAuthentification;
import net.maikydev.nestserver.ducket.annotations.http_types.GetRequest;
import net.maikydev.nestserver.ducket.annotations.RequestMapping;
import net.maikydev.nestserver.ducket.annotations.RequestParam;
import net.maikydev.nestserver.ducket.utils.Pair;

import java.io.IOException;

@RequestMapping("/test")
public class ExampleRoute {

    @GetRequest
    @RequestMapping("/get")
    public DuckletResponse exampleOfGet(@RequestBody JsonEntity body) {
        if (body == null)
            return DuckletResponse.badRequest().sendJson("error", "NoJson!");
        DuckletRequest newRequest = new DuckletRequest();
        newRequest.setUrl("http://10.0.0.200/api/v1/sensor/read");
        JsonObject object = (JsonObject) body;
        for (JsonValue<String, Object> downstream : object.getDownstream()) {
            if (downstream.getValue() == null)
                continue;
            newRequest.addHttpParam(downstream.getKey(), downstream.getValue().toString());
        }
        try {
            return newRequest.makeHttpRequest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
