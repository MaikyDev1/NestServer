package net.maikydev.nestserver.features.tasks;

import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;

public interface Timing {

    boolean itsTheTimeToRun();
    void saveToConfig(YamlConfig config, String path);
    JsonObject getTimingDetails();

}
