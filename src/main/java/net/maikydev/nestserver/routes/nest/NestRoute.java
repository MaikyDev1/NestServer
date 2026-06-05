package net.maikydev.nestserver.routes.nest;

import eu.duckee.duckletwebserver.annotations.http_types.GetRequest;
import eu.duckee.duckletwebserver.annotations.request.RequestMapping;
import eu.duckee.duckletwebserver.annotations.request.RequestUrlParam;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;
import net.maikydev.duckycore.data.json.objects.JsonArray;
import net.maikydev.nestserver.NestServer;
import net.maikydev.nestserver.features.nests.Nest;

@RequestMapping("/api/v1/nests")
public class NestRoute {

    @GetRequest
    @RequestMapping("/header/all")
    public DuckletResponse getHeadersForAll() {
        JsonArray array = JsonArray.newJsonArray();
        NestServer.SERVER.getNestRegistry().getNests().values().forEach((nest) -> array.addValues(nest.toHeaderJsonObject()));
        return DuckletResponse.ok().sendJson(array);
    }

    @GetRequest
    @RequestMapping("/header/[nest]")
    public DuckletResponse getHeaders(@RequestUrlParam("nest") String nestKey) {
        Nest nest = NestServer.SERVER.getNestRegistry().getNest(nestKey);

        if (nest == null)
            return DuckletResponse.notFound().sendJson("error", "Nest not found!");

        return DuckletResponse.ok().sendJson(nest.toHeaderJsonObject());
    }

    @GetRequest
    @RequestMapping("/get/[nest]")
    public DuckletResponse getFullNest(@RequestUrlParam("nest") String nestKey) {
        Nest nest = NestServer.SERVER.getNestRegistry().getNest(nestKey);

        if (nest == null)
            return DuckletResponse.notFound().sendJson("error", "Nest not found!");

        return DuckletResponse.ok().sendJson(nest.toJsonObject());
    }

}
