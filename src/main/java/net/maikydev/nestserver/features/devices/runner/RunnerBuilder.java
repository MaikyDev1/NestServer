package net.maikydev.nestserver.features.devices.runner;

import net.maikydev.duckycore.data.yaml.YamlConfig;

public interface RunnerBuilder {

    String getType();

    Runner createRunner(YamlConfig config, String path) throws NullPointerException;

}
