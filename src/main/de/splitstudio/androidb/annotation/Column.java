package de.splitstudio.androidb.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target( { FIELD })
@Retention(value = RUNTIME)
public @interface Column {

	boolean notNull() default false;

	boolean autoIncrement() default false;

	boolean primaryKey() default false;

}
