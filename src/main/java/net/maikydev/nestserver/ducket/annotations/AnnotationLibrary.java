package net.maikydev.nestserver.ducket.annotations;

import lombok.Getter;
import net.maikydev.nestserver.ducket.annotations.auth.Authentication;
import net.maikydev.nestserver.ducket.annotations.auth.RequireAuthentification;

import java.lang.annotation.Annotation;

public enum AnnotationLibrary {
    REQUEST_MAPPING (RequestMapping.class),
    AUTHENTIFICATION (Authentication.class),
    REQUIRE_AUTHENTIFICATION(RequireAuthentification.class),
    REQUEST_BODY(RequestBody.class),
    REQUEST_PARAM(RequestParam.class),
    REQUEST_URL_PARAM(RequestUrlParam.class);

    @Getter
    private final Class<?> annotationClass;
    AnnotationLibrary(Class<?> clazz) {
        this.annotationClass = clazz;
    }

    public static AnnotationLibrary getFromAnnotation(Annotation annotation) {
        if (annotation == null)
            return null;
        Class<?> annotationType = annotation.annotationType();
        for (AnnotationLibrary annotations : values()) {
            if (annotations.annotationClass.equals(annotationType)) {
                return annotations;
            }
        }
        return null;
    }

}
