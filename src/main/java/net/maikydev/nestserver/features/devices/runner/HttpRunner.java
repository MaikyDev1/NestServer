package net.maikydev.nestserver.features.devices.runner;

import net.maikydev.duckycore.data.json.DuckyJson;
import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.nestserver.exceptions.ActionHandleException;
import net.maikydev.nestserver.utils.HttpUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class HttpRunner implements Runner {

    private final String url;
    private final HashMap<String, String> params;
    private final String method;
    private final String responseType;

    public HttpRunner(String url, HashMap<String, String> params, String method, String responseType) throws NullPointerException {
        if (url == null) throw new NullPointerException("Url cannot be null");
        this.url = url;
        this.params = params;
        this.method = method == null ? "GET" : method.toUpperCase(Locale.ROOT);
        this.responseType = responseType == null ? "FORWARD" : responseType.toUpperCase(Locale.ROOT);
    }

    @Override
    public JsonObject handle(HashMap<String, String> placeholders) throws ActionHandleException {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(this.buildUrl(placeholders)).GET().build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Body: " + response.body());
            return switch (responseType) {
                case "FORWARD" -> (JsonObject) DuckyJson.serialization(response.body());
                case "NOTHING" -> JsonObject.newJsonObject();
                default ->
                        JsonObject.newJsonObject().addNewField("error", "Please set a response.mode (forward, custom or nothing)");
            };
        } catch (InterruptedException | IOException e) {
            return JsonObject.newJsonObject().addNewField("error", "We got an error: " + ((Exception)e).getMessage());
        }
    }

    private URI buildUrl(HashMap<String, String> placeholders) throws MalformedURLException {
        if (this.params.isEmpty())
            return URI.create(HttpUtils.applyPlaceholders(url, placeholders));

        StringBuilder newUrl = new StringBuilder(url);
        newUrl.append("?");

        for(String key : this.params.keySet()) {
            newUrl.append(URLEncoder.encode(HttpUtils.applyPlaceholders(key, placeholders), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(HttpUtils.applyPlaceholders(this.params.get(key), placeholders), StandardCharsets.UTF_8)).append("&");
        }

        return URI.create(HttpUtils.applyPlaceholders(newUrl.toString(), placeholders));
    }

    @Override
    public boolean validateArguments(HashMap<String, String> arguments) throws ActionHandleException {
        return false;
    }

}
