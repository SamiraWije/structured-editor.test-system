package ru.ipo.structurededitor.model;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.10.2010
 * Time: 16:20:11
 * To change this template use File | Settings | File Templates.
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DSLBeanParams {
    String shortcut();

    String description() default "";
}
