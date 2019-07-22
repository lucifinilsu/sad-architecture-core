package com.sad.architecture.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AppComponent {

    String name();

    int priority() default 1;

    int threadEnvironment() default TheradEnvironment.MAIN;

}
