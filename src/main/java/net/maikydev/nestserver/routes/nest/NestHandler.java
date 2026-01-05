package net.maikydev.nestserver.routes.nest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.nests.Nest;
import net.maikydev.nestserver.utils.HttpUtils;

import java.io.IOException;

public class NestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String urlPath = exchange.getRequestURI().getPath();
        if (urlPath.equalsIgnoreCase("/api/v1/nests")) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "No API route by this name!"));
            return;
        }
        String[] url = exchange.getRequestURI().getPath().split("/api/v1/nests/")[1].split("/");

        try {
            if (url.length == 2)
                if (url[0].equalsIgnoreCase("header")) {
                    this.handleHeaderGetNests(exchange, url[1]);
                    return;
                } else if (url[0].equalsIgnoreCase("get")) {
                    this.handleGetNest(exchange, url[1]);
                    return;
                }
        } catch (Exception e) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "While running we got: " + e.getMessage()));
            e.printStackTrace();
            return;
        }

        HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "No API route by this name!"));
    }

    private void handleHeaderGetNests(HttpExchange exchange, String key) throws IOException {
        if (key.equalsIgnoreCase("all")) {
            JsonArray array = JsonArray.newJsonArray();
            NestServer.SERVER.getNestRegistry().getNests().values().forEach((nest) -> array.addValues(nest.toHeaderJsonObject()));
            HttpUtils.respondWithJson(exchange, 200, array);
            return;
        }

        Nest nest = NestServer.SERVER.getNestRegistry().getNest(key);
        if (nest != null) {
            HttpUtils.respondWithJson(exchange, 200, nest.toHeaderJsonObject());
            return;
        }
        HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "The nest was not found!"));
    }

    private void handleGetNest(HttpExchange exchange, String nestKey) throws IOException {
        Nest nest = NestServer.SERVER.getNestRegistry().getNest(nestKey);
        if (nest != null) {
            HttpUtils.respondWithJson(exchange, 200, nest.toJsonObject());
            return;
        }
        HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "The nest was not found!"));
    }

}
