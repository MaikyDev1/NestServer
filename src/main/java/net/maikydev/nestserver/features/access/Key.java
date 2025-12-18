package net.maikydev.nestserver.features.access;

import net.maikydev.duckycore.data.yaml.YamlConfig;

import java.util.List;

public class Key {

    private String key;
    private List<String> access;

    protected static Key wrapFromConfig(YamlConfig config, String path) {
        Key key = new Key();
        key.key = config.getString(path + ".key");
        key.access = config.getStringList(path + ".access");
        return key;
    }

    protected boolean canAccess(String key, String location) {
        if (!this.key.equals(key)) return false;
        if (access.contains("all")) return true;
        if (access.contains(location)) return true;
        return false;
    }

}
