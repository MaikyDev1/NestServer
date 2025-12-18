package net.maikydev.nestserver.features.access;

import net.maikydev.duckycore.data.yaml.YamlConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AccessController {

    private List<Key> keys;
    private HashMap<String, Account> accounts;
    private AccessType accessType;

    public static AccessController wrapFromConfig(YamlConfig config) {
        AccessController controller = new AccessController();
        controller.accessType = AccessType.valueOf(config.getString("security.mode").toUpperCase(Locale.ROOT));
        if (controller.accessType == AccessType.KEY) {
            controller.keys = config.getSubConfiguration("security.keys").keySet().stream()
                    .map(k -> Key.wrapFromConfig(config, "security.keys." + k)).toList();
        }
        // accounts
        if (config.contains("accounts")) {
            controller.accounts = new HashMap<>();
            config.getSubConfiguration("account").keySet().forEach(key -> {
                if (config.contains("account." + key + ".password"))
                    controller.accounts.put(key, new Account(
                            config.getString("account." + key + ".username"),
                            config.getString("account." + key + ".password"),
                            null
                    ));
                if (config.contains("account." + key + ".key"))
                    controller.accounts.put(key, new Account(
                            null,
                            null,
                            config.getString("account." + key + ".key")
                    ));
            });
        }
        return controller;
    }

    public Account getLoginAccount(String accountId) {
        if (accounts != null && accounts.containsKey(accountId))
            return accounts.get(accountId);
        return null;
    }

    public boolean canAccessLocation(String key, String location) {
        if (accessType == AccessType.NONE) return true;
        if (accessType == AccessType.KEY && keys != null) {
            for (Key k : keys)
                if (k.canAccess(key, location))
                    return true;
        }
        return false;
    }

    private enum AccessType {
        NONE,
        KEY;
    }

}
