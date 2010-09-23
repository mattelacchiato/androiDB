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

	/**
	 * Foreign Keys are only available in SQLite 3.6.19, which was first used in Android 2.2 (API-Level 8).
	 * 
	 * @see http://www.sqlite.org/foreignkeys.html
	 * @return
	 */
	//String foreignKey() default "";

}
