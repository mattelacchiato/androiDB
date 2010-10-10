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
package de.splitstudio.androidb;

import java.lang.reflect.Field;

import android.database.Cursor;

/**
 * Maps {@link java.lang.reflect.Field}'s type to SQL types and vice versa.
 * 
 * @author Matthias Brandt
 * @since 2010
 */
public class TypeMapper {

	public static final String BLOB = "BLOB";

	public static final String TEXT = "TEXT";

	public static final String REAL = "REAL";

	public static final String INTEGER = "INTEGER";

	public static final String NULL = "NULL";

	public static String getSqlType(final Class<?> type) {
		if (type == null) {
			return NULL;
		}
		if (type.equals(Long.class) || type.equals(Integer.class) || type.equals(Short.class)
				|| type.equals(Byte.class) || type.equals(Long.class) || type.equals(int.class)
				|| type.equals(short.class) || type.equals(byte.class) || type.equals(long.class)) {
			return INTEGER;
		}
		if (type.equals(Float.class) || type.equals(Double.class) || type.equals(float.class)
				|| type.equals(double.class)) {
			return REAL;
		}
		if (type.equals(String.class)) {
			return TEXT;
		}

		return BLOB;
	}

	/**
	 * Reads the typed value from the cursor and set in into field. The db-type will be guessed by the field type.<br/>
	 * This method *must* lay in this class to access the protected {@link #setValue(Field, Object)}.
	 * 
	 * @param c cursor pointing on its first and hopefully only entry.
	 * @param table the table, which's field should be set.
	 * @param field the field which should be filled.
	 */
	public static void setTypedValue(final Cursor c, final Table table, final Field field)
			throws IllegalArgumentException, IllegalAccessException {
		int index = c.getColumnIndex(field.getName());
		Class<?> type = field.getType();
		field.setAccessible(true);

		if (type.equals(long.class) || type.equals(Long.class)) {
			field.set(table, c.getLong(index));
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			field.set(table, c.getInt(index));
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			field.set(table, c.getShort(index));
		} else if (type.equals(byte.class) || type.equals(Byte.class)) {
			field.set(table, (byte) c.getShort(index));
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			field.set(table, c.getDouble(index));
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			field.set(table, c.getFloat(index));
		} else if (type.equals(char.class)) {
			field.set(table, (char) c.getShort(index));
		} else if (type.equals(String.class)) {
			field.set(table, c.getString(index));
		}
	}

}
