package net.maikydev.nestserver.features.devices.runner;

import net.maikydev.duckycore.data.yaml.YamlConfig;

import java.util.HashMap;

public class HttpRunnerBuilder implements RunnerBuilder {

    @Override
    public String getType() {
        return "http";
    }

    @Override
    public Runner createRunner(YamlConfig config, String path) throws NullPointerException {
        HashMap<String, String> params = new HashMap<>();
        config.getSubConfiguration(path + ".params").forEach((k, v) ->
                params.put(k, String.valueOf(v)));
        return new HttpRunner(
                config.getString(path + ".url"),
                params,
                config.contains(path + ".method") ? config.getString(path + ".method") : null,
                config.getString(path + ".response.mode")
        );
    }

}
