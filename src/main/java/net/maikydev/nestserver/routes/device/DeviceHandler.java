package net.maikydev.nestserver.routes.device;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.nestserver.features.devices.Device;
import net.maikydev.nestserver.features.devices.DeviceRegistry;
import net.maikydev.nestserver.utils.HttpUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class DeviceHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String urlPath = exchange.getRequestURI().getPath();
        if (urlPath.equalsIgnoreCase("/api/v1/device")) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "No API route by this name!"));
            return;
        }
        String[] url = exchange.getRequestURI().getPath().split("/api/v1/device/")[1].split("/");
        System.out.println(Arrays.toString(url));
        System.out.println(url.length);

        try {
            if (url.length == 1) {
                this.handleGetDevice(exchange, url[0]);
                return;
            }

            if (url.length == 3 && url[1].equals("run")) {
                this.handleRunAction(exchange, url[0], url[2]);
                return;
            }
        } catch (Exception e) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "While running we got: " + e.getMessage()));
            e.printStackTrace();
            return;
        }

        HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "No API route by this name!"));
    }

    private void handleGetDevice(HttpExchange exchange, String key) throws IOException {
        if (key.equalsIgnoreCase("all")) {
            JsonArray array = JsonArray.newJsonArray();
            DeviceRegistry.DEVICE.getDevices().values().forEach((device) -> array.addValues(this.getDeviceDetails(device)));
            HttpUtils.respondWithJson(exchange, 200, array);
            return;
        }

        Device device = DeviceRegistry.DEVICE.getDevice(key);
        if (device != null) {
            HttpUtils.respondWithJson(exchange, 200, this.getDeviceDetails(device));
            return;
        }
        HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "The device was not found!"));
    }

    private JsonObject getDeviceDetails(Device device) {
        JsonArray array = JsonArray.newJsonArray();
        device.getActions().keySet().forEach(array::addValues);
        return JsonObject.newJsonObject()
                .addNewField("id", device.getId())
                .addNewField("actions", array);
    }

    private void handleRunAction(HttpExchange exchange, String deviceKey, String actionKey) throws IOException {
        Device device = DeviceRegistry.DEVICE.getDevice(deviceKey);
        if (device == null) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "The device was not found!"));
            return;
        }
        if (!device.hasAction(actionKey)) {
            HttpUtils.respondWithJson(exchange, 404, JsonObject.newJsonObject().addNewField("error", "The action was not found!"));
            return;
        }
        HttpUtils.respondWithJson(exchange, 200, device.runAction(actionKey, HttpUtils.getQueryParams(exchange.getRequestURI().getQuery())));
    }

}
