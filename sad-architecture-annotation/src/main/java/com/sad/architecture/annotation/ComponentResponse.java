package com.sad.architecture.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;

/**
 * Created by Administrator on 2019/3/22 0022.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentResponse{

    int threadEnvironment() default TheradEnvironment.MAIN;

    int priority() default 1;

    String componentName();

    String path() default "";
}
