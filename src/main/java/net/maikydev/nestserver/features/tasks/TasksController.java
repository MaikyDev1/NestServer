package net.maikydev.nestserver.features.tasks;

import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.NestServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
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

    /**
     * Save a new task into the data.yml and in the memory.
     * @param group The group for the task
     * @param task The task object to be runed and saved!
     */
    public void addTask(String group, Task task) {
        task.saveToConfig(NestServer.SERVER.getData(), "tasks." + group);
        if (tasks.containsKey(group)) {
            tasks.get(group).add(task);
            return;
        }
        tasks.put(group, new ArrayList<>());
        tasks.get(group).add(task);
    }

    /**
     * Removed from config and from the internal memory
     * @param group The group for the task
     * @param id The id for the task
     */
    public void removeTask(String group, String id) {
        if (!hasTaskByGroup(group, id))
            return;
        NestServer.SERVER.getData().getSubConfiguration("tasks." + group).remove(id);
        NestServer.SERVER.getData().saveConfig();
        tasks.get(group).removeIf(t -> t.getId().equals(id));
    }

    /**
     * Remove an entire group with all its tasks!
     * @param group the group name!
     */
    public void removeGroup(String group) {
        if (!hasGroup(group))
            return;
        NestServer.SERVER.getData().getSubConfiguration("tasks").remove(group);
        NestServer.SERVER.getData().saveConfig();
        tasks.remove(group);
    }

    public boolean hasGroup(String group) {
        return tasks.containsKey(group);
    }

    public Set<String> getGroups() {
        return tasks.keySet();
    }

    public boolean hasTaskByGroup(String group, String task) {
        return hasGroup(group) && getTaskListByGroup(group).stream().anyMatch(t-> t.getId().equals(task));
    }

    public ArrayList<Task> getTaskListByGroup(String group) {
        return tasks.get(group);
    }

    public Task getTaskByGroup(String group, String task) {
        if (hasGroup(group)) {
            Optional<Task> op = getTaskListByGroup(group).stream().filter(t-> t.getId().equals(task)).findFirst();
            if (op.isPresent()) return op.get();
        }
        return null;
    }

}
