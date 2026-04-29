package net.maikydev.nestserver;

import lombok.Getter;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.ducket.ducklet.DuckletController;
import net.maikydev.nestserver.ducklettest.ExampleRoute;
import net.maikydev.nestserver.features.access.AccessController;
import net.maikydev.nestserver.features.devices.DeviceRegistry;
import net.maikydev.nestserver.features.nests.NestRegistry;
import net.maikydev.nestserver.features.sceans.SceneRegistry;
import net.maikydev.nestserver.features.tasks.TasksController;
import net.maikydev.nestserver.routes.admin.TasksRoute;
import net.maikydev.nestserver.routes.device.DeviceRoute;
import net.maikydev.nestserver.routes.nest.NestRoute;
import net.maikydev.nestserver.routes.scene.SceneRoute;

@Getter
public enum NestServer {
    SERVER;

    private YamlConfig config;
    private YamlConfig devicesConfig;
    private YamlConfig nestsConfig;
    private YamlConfig data;

    private AccessController accessController;
    private SceneRegistry sceneRegistry;
    private NestRegistry nestRegistry;
    private TasksController tasksController;

    public void onStart() {
        this.config = YamlConfig.fromFileName("config.yml");
        this.devicesConfig = YamlConfig.fromFileName("devices.yml");
        this.nestsConfig = YamlConfig.fromFileName("nests.yml");
        this.data = YamlConfig.fromFileName("data.yml");
//        this.config = YamlConfig.fromFile(new File("config.yml"));
//        this.devicesConfig = YamlConfig.fromFile(new File("devices.yml"));
//        this.nestsConfig = YamlConfig.fromFile(new File("nests.yml"));
//        this.data = YamlConfig.fromFile(new File("data.yml"));
        loadConfigs();
        tasksController.start();
        DuckletController controller = DuckletController.createController(8080, 10);
        if (controller == null)
            return;
        controller.addRoute(new ExampleRoute());
        controller.addRoute(new DeviceRoute());
        controller.addRoute(new NestRoute());
        controller.addRoute(new SceneRoute());
        controller.addRoute(new TasksRoute(tasksController));
        controller.startController();
    }

    private void loadConfigs() {
        accessController = AccessController.wrapFromConfig(config);
        DeviceRegistry.DEVICE.addDevices(devicesConfig, "devices");
        sceneRegistry = new SceneRegistry().addScenes(nestsConfig, "scenes");
        nestRegistry = new NestRegistry().addNests(nestsConfig, "nests");
        tasksController = new TasksController().loadTasks(data, "tasks");
    }


}
