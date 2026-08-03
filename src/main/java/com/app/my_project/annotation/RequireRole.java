package com.app.my_project.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Restricts an endpoint to the listed roles, read from the "role" claim of the JWT.
// Without it an endpoint only requires a valid token (any signed-in user), which is
// the right default for the shared modules: inventory, production and sales.
//
// Applied to a class it covers every endpoint in that controller; a method-level
// annotation wins over the class-level one.
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value();
}
