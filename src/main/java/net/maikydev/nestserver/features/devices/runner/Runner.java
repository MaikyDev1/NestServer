package net.maikydev.nestserver.features.devices.runner;

import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.nestserver.exceptions.ActionHandleException;

import java.util.HashMap;

public interface Runner {
    /**
     * The action interface handle is the function to call when have to
     * run the action with this type, for each type you have to give arguments
     * @param placeholders
     * @return
     */
    JsonObject handle(HashMap<String, String> placeholders) throws ActionHandleException;

    /**
     * Because every runner has its own arguments and a lot of stuff
     * Any implementation of the function has to implement a validateArguments method
     * to check if the arguments are correct.
     * @param placeholders Arguments
     * @return true if the arguments are good
     * @throws ActionHandleException if the arguments are wrong and a message.
     */
    boolean validateArguments(HashMap<String, String> placeholders) throws ActionHandleException;
}
