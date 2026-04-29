package net.maikydev.nestserver.routes.admin;

import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.nestserver.ducket.exchange.DuckletResponse;
import net.maikydev.nestserver.ducket.annotations.RequestBody;
import net.maikydev.nestserver.ducket.annotations.RequestMapping;
import net.maikydev.nestserver.ducket.annotations.RequestUrlParam;
import net.maikydev.nestserver.ducket.annotations.http_types.GetRequest;
import net.maikydev.nestserver.ducket.annotations.http_types.PostRequest;
import net.maikydev.nestserver.features.tasks.Task;
import net.maikydev.nestserver.features.tasks.TasksController;

@RequestMapping("/api/v1/tasks")
public class TasksRoute {

    private final TasksController tasksController;

    public TasksRoute(TasksController tasksController) {
        this.tasksController = tasksController;
    }

    @GetRequest
    @RequestMapping("/get/all")
    public DuckletResponse getAllGroups() {
        JsonObject jsonObject = JsonObject.newJsonObject();
        for (String group : tasksController.getGroups()) {
            JsonArray groupJson = JsonArray.newJsonArray();
            tasksController.getTaskListByGroup(group).forEach(task -> groupJson.addValues(getTaskDetails(task)));
            jsonObject.addNewField(group, groupJson);
        }
        return DuckletResponse.ok().sendJson(jsonObject);
    }

    @GetRequest
    @RequestMapping("/get/[group]/[task]")
    public DuckletResponse getGroup(@RequestUrlParam("group") String groupKey, @RequestUrlParam("task") String taskKey) {
        if (!tasksController.hasTaskByGroup(groupKey, taskKey))
            return DuckletResponse.notFound().sendJson("error", "We did not found task or group by this id!");
        return DuckletResponse.ok().sendJson(getTaskDetails(tasksController.getTaskByGroup(groupKey, taskKey)));
    }

    private JsonObject getTaskDetails(Task task) {
        JsonArray cmdArray = JsonArray.newJsonArray();
        task.getCommands().forEach(cmdArray::addValues);
        return JsonObject.newJsonObject()
                .addNewField("id", task.getId())
                .addNewField("commands", cmdArray)
                .addNewField("schedule", task.getTiming().getTimingDetails());
    }

    @PostRequest
    @RequestMapping("/new")
    public DuckletResponse newTask(@RequestBody JsonObject object) {
        if (object == null)
            return DuckletResponse.badRequest().sendJson("error", "You must include a JSON body with the task configuration!");
        tasksController.addTask(object.findKey("group").getString(), Task.wrapTaskFromJson(object));
        return DuckletResponse.ok().sendJson("error", "Saved new task!");
    }

    @PostRequest
    @RequestMapping("/delete/[group]/[task]")
    public DuckletResponse deleteTask(@RequestUrlParam("group") String groupKey, @RequestUrlParam("task") String taskKey) {
        if (!tasksController.hasTaskByGroup(groupKey, taskKey))
            return DuckletResponse.notFound().sendJson("error", "We did not found task or group by this id!");
        tasksController.removeTask(groupKey, taskKey);
        return DuckletResponse.ok().sendJson("success", "Saved new task!");
    }

    @PostRequest
    @RequestMapping("/delete/[group]]")
    public DuckletResponse deleteAllGroup(@RequestUrlParam("group") String groupKey) {
        if (tasksController.removeGroup(groupKey))
            return DuckletResponse.ok().sendJson("success", "Removed group!");
        return DuckletResponse.notFound().sendJson("error", "Group not found!");
    }

}
