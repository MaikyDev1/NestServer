package net.maikydev.nestserver.utils;

import com.sun.net.httpserver.HttpExchange;
import net.maikydev.duckycore.data.json.DuckyJson;
import net.maikydev.duckycore.data.json.objects.JsonEntity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {

    public static String addParamsToURI(String uri, Map<String, Object> params) {
        if (params == null) {
            return uri;
        } else {
            StringBuilder uriBuilder = new StringBuilder(uri);
            uriBuilder.append("?");
            params.forEach((key, value) -> uriBuilder.append(key).append("=").append(value).append("&"));
            return uriBuilder.toString();
        }
    }

    public static HashMap<String, String> getQueryParams(String params) {
        if (params == null) {
            return null;
        } else {
            Pattern queryPattern = Pattern.compile("&?([^=]+)=([^&]+)");
            Matcher matcher = queryPattern.matcher(params);
            HashMap<String, String> map = new HashMap();

            while(matcher.find()) {
                map.put(matcher.group(1), matcher.group(2));
            }

            System.out.println(map);
            return map;
        }
    }

    public static void respondWithJson(HttpExchange exchange, int code, JsonEntity json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        String jsonString = DuckyJson.deserialization(json);
        System.out.println(jsonString);
        exchange.sendResponseHeaders(code, (long)jsonString.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonString.getBytes());
        os.close();
    }

    public static String[] getURIPath(String base, URI baseURI) {
        String nonBaseURI = baseURI.getPath().split(base)[1];
        return nonBaseURI == null ? null : nonBaseURI.split("/");
    }

    public static String applyPlaceholders(String s, HashMap<String, String> placeholder) {
        if (placeholder == null)
            return s;

        String newString = s;
        for(String key : placeholder.keySet()) {
            newString = newString.replaceAll("%" + key + "%", placeholder.get(key));
        }
        System.out.println(newString);
        return newString;
    }

}
