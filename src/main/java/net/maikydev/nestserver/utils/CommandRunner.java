package net.maikydev.nestserver.utils;

import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.devices.DeviceRegistry;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandRunner {

    private final static Pattern CMD_PATTERN = Pattern.compile("\\[([^]]*)]\\W?(\\S+)\\W?(\\S+)?");

    /**
     * This command will run all the commands from a string bypassing ANY security!
     * The command list should be:
     * [RUN] (device) (action)
     * [SCENE] (scene) {state (if necessary)}
     * @param commands Some list with commands
     */
    public static void runCommands(List<String> commands) {
        for (String command : commands) {
            Matcher matcher = CMD_PATTERN.matcher(command);
            if (!matcher.find())
                continue;
            switch (matcher.group(1)) {
                case "SAY": {
                    if (!DeviceRegistry.DEVICE.deviceExists(matcher.group(2))) {
                        break;
                    }
                    break;
                }
                case "RUN":
                case "DEVICE": {
                    if (!DeviceRegistry.DEVICE.deviceExists(matcher.group(2))) continue;
                    DeviceRegistry.DEVICE.getDevice(matcher.group(2)).runAction(matcher.group(3), null);
                    break;
                }
                case "SCENE": {
                    if (!NestServer.SERVER.getSceneRegistry().sceneExists(matcher.group(2))) continue;
                    NestServer.SERVER.getSceneRegistry().getScene(matcher.group(2)).turnState();
                    break;
                }
            }
        }
    }

}
