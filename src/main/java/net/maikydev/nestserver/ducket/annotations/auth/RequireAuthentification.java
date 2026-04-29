package net.maikydev.nestserver.ducket.annotations.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequireAuthentification {

}
