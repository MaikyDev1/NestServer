package net.maikydev.nestserver.ducket.ducklet;

import com.sun.net.httpserver.HttpExchange;
import net.maikydev.nestserver.ducket.HttpMethod;
import net.maikydev.nestserver.ducket.annotations.AnnotationLibrary;
import net.maikydev.nestserver.ducket.annotations.RequestMapping;
import net.maikydev.nestserver.ducket.exception.DuckletHandlerException;
import net.maikydev.nestserver.ducket.exchange.DuckletRequest;
import net.maikydev.nestserver.ducket.utils.Mapping;
import net.maikydev.nestserver.ducket.utils.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DuckletHandler {

    private Object ducklet;
    private String mapping;
    private List<DuckletEndpoint> methods;

    /**
     * Internal process for creating routes from instances
     * @param object The ducklet instance
     * @return The wrapped handler of the instance
     * @throws DuckletHandlerException Any bad implementation will result in a failer!
     */
    protected static DuckletHandler wrapFromRoute(Object object) throws DuckletHandlerException {
        DuckletHandler dh = new DuckletHandler();
        Class<?> clazz = object.getClass();
        if (!clazz.isAnnotationPresent(RequestMapping.class))
            throw new DuckletHandlerException("An object with no RequestMapping was provided!");
        dh.mapping = clazz.getAnnotation(RequestMapping.class).value();
        dh.methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods())
            dh.addMethod(method);
        dh.ducklet = object;
        return dh;
    }

    private void addMethod(Method method) throws DuckletHandlerException {
        if (!method.isAnnotationPresent(RequestMapping.class))
            return;
        DuckletEndpoint endpoint = new DuckletEndpoint();
        endpoint.setMethod(method);
        for (Annotation annotation : method.getAnnotations()) {
            AnnotationLibrary annot = AnnotationLibrary.getFromAnnotation(annotation);
            if (annot == AnnotationLibrary.REQUEST_MAPPING) {
                endpoint.setMapping(Mapping.wrapFromString(method.getAnnotation(RequestMapping.class).value()));
            }
            HttpMethod httpMethod = HttpMethod.getMethod(annotation);
            if (httpMethod == null)
                continue;
            endpoint.setHttpMethod(httpMethod);
        }
        if (endpoint.getHttpMethod() == null)
            throw new DuckletHandlerException("Method: " + method.getName() + " from class " + method.getClass() + " has a @RequestMapping but no HttpMethod (Like @GetRequest)!");
        // Why this code? We are adding dinamic routes to the end of the array so we can make custom routes beside dynamic ones!
        if (endpoint.getMapping().isDynamic())
            methods.addLast(endpoint);
        else
            methods.addFirst(endpoint);
    }

    protected void handle(HttpExchange exchange, String url) throws IOException, DuckletHandlerException {
        System.out.println(url);
        Map<String, String> query = null;
        String queryString = exchange.getRequestURI().getQuery();
        if (queryString != null && !queryString.isEmpty()) {
            query = new HashMap<>();
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                query.put(
                        URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : ""
                        );
            }
        }
        for (DuckletEndpoint endpoint : methods) {
            Pair<Boolean, Map<String, String>> tagLines = endpoint.getMapping().matchAndGetTagLines(url);
            if (!tagLines.getKey())
                continue;
            if (exchange.getRequestMethod().equalsIgnoreCase(endpoint.getHttpMethod().toString())) {
                DuckletRequest request = new DuckletRequest();
                String body = null;
                InputStream is = exchange.getRequestBody();
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    if (bytes.length > 0) {
                        body = new String(bytes, StandardCharsets.UTF_8);
                    }
                }
                request.addTagLines(tagLines.getValue()).addHttpParams(query).setHttpBody(body);
                endpoint.handle(ducklet, request).respond(exchange);
                return;
            }
        }
    }

}
