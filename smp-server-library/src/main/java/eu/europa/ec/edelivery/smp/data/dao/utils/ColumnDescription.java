package eu.europa.ec.edelivery.smp.data.dao.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotation is just for addding column description. At generation DDL description is handled by hibernate
 * It seems that hbm supports column description but annotations does not!  - hibernate version 5.4.0.Final
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnDescription {
    String comment() default "";
}
