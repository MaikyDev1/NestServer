package net.maikydev.nestserver.ducket.annotations.auth;

import net.maikydev.nestserver.ducket.auth.AuthentificationProfile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authentication {
}
