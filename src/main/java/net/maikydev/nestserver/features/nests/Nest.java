package net.maikydev.nestserver.features.nests;

import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.nests.elements.Element;
import net.maikydev.nestserver.utils.HtmlMeta;

import java.util.ArrayList;
import java.util.List;

public interface Nest {

    JsonObject toJsonObject();

}
