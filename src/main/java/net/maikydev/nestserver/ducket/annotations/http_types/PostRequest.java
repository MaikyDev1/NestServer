package net.maikydev.nestserver.ducket.annotations.http_types;

import net.maikydev.nestserver.ducket.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PostRequest {
}
