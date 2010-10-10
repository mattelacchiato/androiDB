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

import java.lang.reflect.Field;

/**
 * Some helper methods for {@link Column}.
 * 
 * @author Matthias Brandt
 * @since 2010
 */
public class ColumnHelper {

	/**
	 * Creates a new StringBuilder containing all constraints needed for the SQL table definition.
	 * 
	 * @param field the field, which may contains some constraints.
	 * @return all constraints with a leading space. Empty StringBuilder, when no constraints were found.
	 */
	public static StringBuilder getConstraints(final Field field) {
		StringBuilder constraints = new StringBuilder();
		Column column = field.getAnnotation(Column.class);

		if (column.primaryKey()) {
			constraints.append(" PRIMARY KEY");
		}
		if (column.autoIncrement()) {
			constraints.append(" AUTOINCREMENT");
		}
		if (column.notNull()) {
			constraints.append(" NOT NULL");
		}
		return constraints;
	}

	/**
	 * Checks if this field is a {@link Column}.
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isColumn(final Field field) {
		return field.getAnnotation(Column.class) != null;
	}

}
