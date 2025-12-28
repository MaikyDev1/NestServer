package net.maikydev.nestserver.features.nests.elements;

import net.maikydev.duckycore.data.json.objects.JsonObject;

public interface Element {
    int getPriority();
    JsonObject toJsonObject();
}
