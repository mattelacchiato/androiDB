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
import java.lang.reflect.Type;

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

	public static Object getTypedValue(final Cursor cursor, final Field field) {
		int index = cursor.getColumnIndex(field.getName());
		Type type = field.getType();
		if (type.equals(long.class) || type.equals(Long.class)) {
			return cursor.getLong(index);
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			return cursor.getInt(index);
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			return cursor.getShort(index);
		} else if (type.equals(byte.class) || type.equals(Byte.class)) {
			return (byte) cursor.getShort(index);
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return cursor.getDouble(index);
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			return cursor.getFloat(index);
		} else if (type.equals(char.class)) {
			return (char) cursor.getShort(index);
		} else if (type.equals(String.class)) {
			return cursor.getString(index);
		}
		return null;
	}
}
