package net.maikydev.nestserver.routes.device;

import eu.duckee.duckletwebserver.annotations.http_types.GetRequest;
import eu.duckee.duckletwebserver.annotations.request.RequestMapping;
import eu.duckee.duckletwebserver.annotations.request.RequestUrlParam;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;
import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.nestserver.features.devices.Device;
import net.maikydev.nestserver.features.devices.DeviceRegistry;

@RequestMapping("/api/v1/device")
public class DeviceRoute {

    private final DeviceRegistry deviceRegistry = DeviceRegistry.DEVICE;

    @GetRequest
    @RequestMapping("/all")
    public DuckletResponse getAll() {
        JsonArray array = JsonArray.newJsonArray();
        DeviceRegistry.DEVICE.getDevices().values().forEach((device) -> array.addValues(this.getDeviceDetails(device)));
        return DuckletResponse.ok().sendJson(array);
    }

    @GetRequest
    @RequestMapping("/[device]")
    public DuckletResponse getPerDevice(@RequestUrlParam("device") String key) {
        Device device = DeviceRegistry.DEVICE.getDevice(key);
        if (device == null)
            return DuckletResponse.notFound().sendJson("error", "Device not found!");
        return DuckletResponse.ok().sendJson(this.getDeviceDetails(device));
    }

    @GetRequest
    @RequestMapping("/[device]/run/[action]")
    public DuckletResponse runDevice(@RequestUrlParam("device") String deviceKey, @RequestUrlParam("action") String actionKey) {
        Device device = DeviceRegistry.DEVICE.getDevice(deviceKey);
        if (device == null)
            return DuckletResponse.notFound().sendJson("error", "Device not found!");
        if (!device.hasAction(actionKey))
            return DuckletResponse.notFound().sendJson("error", "Action not found!");
        return DuckletResponse.ok().sendJson(device.runAction(actionKey, null));
    }

    private JsonObject getDeviceDetails(Device device) {
        JsonArray array = JsonArray.newJsonArray();
        device.getActions().keySet().forEach(array::addValues);
        return JsonObject.newJsonObject()
                .addNewField("id", device.getId())
                .addNewField("actions", array);
    }

}
