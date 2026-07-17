package com.app.my_project.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Marks an endpoint as reachable WITHOUT a JWT token.
// Everything else is protected by default (see JwtInterceptor) -
// forgetting an annotation fails safe instead of leaving a hole.
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Public {
}
