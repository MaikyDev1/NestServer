package net.maikydev.nestserver.features.tasks;

import lombok.Getter;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.json.objects.JsonValue;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.features.nests.elements.DeviceCall;
import net.maikydev.nestserver.features.sceans.Scene;
import net.maikydev.nestserver.utils.CommandRunner;
import net.maikydev.nestserver.utils.Pair;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Task {

    private String id;
    private boolean oneTime;
    private List<String> commands;
    private Timing timing;

    public static Task wrapTaskFromConfig(YamlConfig config, String path) {
        Task task = new Task();
        task.id = path.substring(path.lastIndexOf('.') + 1);
        task.timing = SchedulerTiming.wrapFromConfig(config, path + ".schedule");
        task.oneTime = config.contains(path + ".one_time") ? config.getBoolean(path + ".one_time") : false;
        task.commands = config.getStringList(path + ".commands");
        return task;
    }

    public static Task wrapTaskFromJson(JsonObject object) {
        Task task = new Task();
        task.id = object.findKey("id").getString();
        task.timing = SchedulerTiming.wrapFromJson(object.findKey("schedule").getObject());
        task.commands = object.findKey("commands").getArray().getObjects().stream().map(k -> k.getDownstream().getFirst().getString()).toList();
        return task;
    }

    public Task setId(String id) {
        this.id = id;
        return this;
    }

    public Task setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
        return this;
    }

    public Task setCommands(List<String> commands) {
        this.commands = commands;
        return this;
    }

    public Task addCommands(String command) {
        if (commands != null)
            commands.add(command);
        else {
            commands = new ArrayList<>();
            commands.add(command);
        }
        return this;
    }

    public Task setTiming(Timing timing) {
        this.timing = timing;
        return this;
    }

    public void saveToConfig(YamlConfig config, String path) {
        config.setData(path + "." + id + ".commands", commands);
        timing.saveToConfig(config, path + "." + id + ".schedule");
        config.saveConfig();
    }

    public void onTick() {
        if (timing.itsTheTimeToRun()) {
            System.out.println("Running: " + id);
            CommandRunner.runCommands(commands);
        }
    }

}
