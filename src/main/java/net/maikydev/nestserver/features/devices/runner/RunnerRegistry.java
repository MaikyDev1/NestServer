package net.maikydev.nestserver.features.devices.runner;

import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.exceptions.ConfigParserException;

import java.util.HashMap;

public class RunnerRegistry {

    private final HashMap<String, RunnerBuilder> runners = new HashMap<>();

    public RunnerRegistry() {
        addRunner(new HttpRunnerBuilder());
    }

    public void addRunner(RunnerBuilder action) {
        runners.put(action.getType(), action);
    }

    public boolean runnerExists(String runner) {
        return runners.containsKey(runner);
    }

    public Runner createRunner(YamlConfig config, String path) throws ConfigParserException {
        if (!runnerExists(config.getString(path + ".type")))
            throw new ConfigParserException("We dont have a type by: " + config.getString(path + ".type"));
        return runners.get(config.getString(path + ".type")).createRunner(config, path);
    }

}
