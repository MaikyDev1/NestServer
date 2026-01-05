package net.maikydev.nestserver.routes.admin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.devices.Device;
import net.maikydev.nestserver.features.devices.DeviceRegistry;
import net.maikydev.nestserver.features.tasks.SchedulerTiming;
import net.maikydev.nestserver.features.tasks.Task;
import net.maikydev.nestserver.utils.HttpUtils;

import java.io.IOException;

public class TasksHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String urlPath = exchange.getRequestURI().getPath();
        if (urlPath.equalsIgnoreCase("/api/v1/tasks")) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "No API route by this name!"));
            return;
        }
        String[] url = exchange.getRequestURI().getPath().split("/api/v1/tasks/")[1].split("/");

        HttpUtils.respondWithJson(exchange, 200, JsonObject.newJsonObject().addNewField("error", "We addded what you wanted :)"));
    }


}
