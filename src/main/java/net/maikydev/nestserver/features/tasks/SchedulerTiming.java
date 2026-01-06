package net.maikydev.nestserver.features.tasks;

import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SchedulerTiming implements Timing {

    private final int hour;
    private final int minute;
    private LocalDateTime newRun;

    public SchedulerTiming(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        updateNewRun();
    }

    public static SchedulerTiming wrapFromConfig(YamlConfig config, String path) {
        String[] time = config.getString(path + ".time").split(":");
        return new SchedulerTiming(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
    }

    public static SchedulerTiming wrapFromJson(JsonObject object) {
        String[] time = object.findKey("time").getString().split(":");
        return new SchedulerTiming(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
    }

    @Override
    public boolean itsTheTimeToRun() {
        LocalDateTime now = LocalDateTime.now();
        if (newRun != null && !now.isAfter(newRun)) return false;
        updateNewRun();
        return true;
    }

    @Override
    public void saveToConfig(YamlConfig config, String path) {
        config.setData(path + ".time", hour + ":" + minute);
    }

    @Override
    public JsonObject getTimingDetails() {
        return JsonObject.newJsonObject().addNewField("time", hour + ":" + minute);
    }

    private void updateNewRun() {
        LocalDateTime now = LocalDateTime.now();
        newRun = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(hour, minute));
        if (newRun.isBefore(now)) {
            newRun = newRun.plusDays(1);
        }
    }

}
