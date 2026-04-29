package net.maikydev.nestserver.ducket;

import net.maikydev.nestserver.ducket.annotations.http_types.GetRequest;
import net.maikydev.nestserver.ducket.annotations.http_types.PostRequest;

import java.lang.annotation.Annotation;

public enum HttpMethod {
    GET(GetRequest.class),
    POST(PostRequest.class);

    private final Class<?> requestClass;
    HttpMethod(Class<?> clazz) {
        this.requestClass = clazz;
    }

    public static HttpMethod getMethod(Annotation annotation) {
        if (annotation == null)
            return null;
        Class<?> annotationType = annotation.annotationType();
        for (HttpMethod method : values()) {
            if (method.requestClass.equals(annotationType)) {
                return method;
            }
        }
        return null;
    }
}
