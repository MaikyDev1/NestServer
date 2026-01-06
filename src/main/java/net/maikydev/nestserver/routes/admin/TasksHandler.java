package net.maikydev.nestserver.routes.admin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.maikydev.duckycore.data.json.DuckyJson;
import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.tasks.Task;
import net.maikydev.nestserver.features.tasks.TasksController;
import net.maikydev.nestserver.utils.HttpUtils;

import java.io.IOException;
import java.util.Arrays;

public class TasksHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String urlPath = exchange.getRequestURI().getPath();
        if (urlPath.equalsIgnoreCase("/api/v1/tasks")) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "No API route by this name!"));
            return;
        }
        String[] url = exchange.getRequestURI().getPath().split("/api/v1/tasks/")[1].split("/");
        System.out.println(Arrays.toString(url));
        switch (url[0]) {
            case "get" -> handleGet(exchange, url);
            case "new" -> handleNew(exchange, url);
            case "delete" -> handleDelete(exchange, url);
            case null, default -> HttpUtils.respondWithJson(exchange, 200, JsonObject.newJsonObject().addNewField("error", "You must not use like this!"));
        }
    }

    private void handleGet(HttpExchange exchange, String[] url) throws IOException {
        if (url.length < 2) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "Please use [.../get/(all|group)/(nothing|task)]"));
            return;
        }
        TasksController controller = NestServer.SERVER.getTasksController();
        if (url[1].equals("all")) {
            JsonObject jsonObject = JsonObject.newJsonObject();
            for (String group : controller.getGroups()) {
                JsonArray groupJson = JsonArray.newJsonArray();
                controller.getTaskListByGroup(group).forEach(task -> groupJson.addValues(getTaskDetails(task)));
                jsonObject.addNewField(group, groupJson);
            }
            HttpUtils.respondWithJson(exchange, 200, jsonObject);
        } else if (url.length == 3) {
            if (!controller.hasTaskByGroup(url[1], url[2])) {
                HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "Not a task by this [.../(group)/(task)]"));
                return;
            }
            HttpUtils.respondWithJson(exchange, 200, getTaskDetails(controller.getTaskByGroup(url[1], url[2])));
        }
    }

    private JsonObject getTaskDetails(Task task) {
        JsonArray cmdArray = JsonArray.newJsonArray();
        task.getCommands().forEach(cmdArray::addValues);
        return JsonObject.newJsonObject()
                .addNewField("id", task.getId())
                .addNewField("commands", cmdArray)
                .addNewField("schedule", task.getTiming().getTimingDetails());
    }

    private void handleNew(HttpExchange exchange, String[] url) throws IOException {
        if (exchange.getRequestBody().available() <= 0) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "You must include a JSON body with the task configuration!"));
            return;
        }
        String s = new String(exchange.getRequestBody().readAllBytes()).replace("\n", "").replace("\t", "");
        if (!s.contains("{")) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "You must include a JSON body with the task configuration!"));
            return;
        }
        JsonObject body = (JsonObject) DuckyJson.serialization(s);
        NestServer.SERVER.getTasksController().addTask(body.findKey("group").getString(), Task.wrapTaskFromJson(body));
        HttpUtils.respondWithJson(exchange, 200, JsonObject.newJsonObject().addNewField("status", "Saved"));
    }

    private void handleDelete(HttpExchange exchange, String[] url) throws IOException {
        if (url.length > 3 || url.length < 2) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "Incorrect arguments [.../delete/(group)/(task)]"));
            return;
        }
        if (url.length == 2) {
            NestServer.SERVER.getTasksController().removeGroup(url[1]);
            HttpUtils.respondWithJson(exchange, 200, JsonObject.newJsonObject().addNewField("status", "Group removed"));
            return;
        }
        NestServer.SERVER.getTasksController().removeTask(url[1], url[2]);
        HttpUtils.respondWithJson(exchange, 200, JsonObject.newJsonObject().addNewField("status", "Task removed"));
    }

}
