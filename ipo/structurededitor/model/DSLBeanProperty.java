package ru.ipo.structurededitor.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 08.11.2010
 * Time: 0:36:13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DSLBeanProperty {
    boolean allowNull() default true;
}
