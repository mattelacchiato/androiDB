/*
 *    Copyright 2010, Matthias Brandt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package de.splitstudio.androidb.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that the annotated field should represent a row in the db. Containing some constraints for
 * SQLite.
 * 
 * @author Matthias Brandt
 * @since 2010
 */
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
