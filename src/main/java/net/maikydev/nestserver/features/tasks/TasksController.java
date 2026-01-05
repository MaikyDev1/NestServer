package net.maikydev.nestserver.features.tasks;

import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.NestServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TasksController {

    private final HashMap<String, ArrayList<Task>> tasks = new HashMap<>();

    public TasksController loadTasks(YamlConfig config, String path) {
        config.getSubConfiguration(path).keySet().forEach((group) -> {
            ArrayList<Task> groupedTasks = new ArrayList<>();
            config.getSubConfiguration(path + "." + group).keySet().forEach((taskId) -> {
                groupedTasks.add(Task.wrapTaskFromConfig(config, path + "." + group + "." + taskId));
            });
            tasks.put(group, groupedTasks);
        });
        return this;
    }

    public void addTask(String group, Task task) {
        task.saveToConfig(NestServer.SERVER.getData(), "tasks." + group);
        if (tasks.containsKey(group)) {
            tasks.get(group).add(task);
            return;
        }
        tasks.put(group, new ArrayList<>());
        tasks.get(group).add(task);
    }

    public void removeTask(String group, String id) {

    }

    private final ScheduledExecutorService ticker =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "tasks-ticking");
                t.setDaemon(true);
                return t;
            });

    public void start() {
        ticker.scheduleAtFixedRate(() -> {
            try {
                tasks.values().forEach(group -> group.forEach(Task::onTick));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

}
