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

import static de.splitstudio.androidb.SqlType.BLOB;
import static de.splitstudio.androidb.SqlType.INTEGER;
import static de.splitstudio.androidb.SqlType.NULL;
import static de.splitstudio.androidb.SqlType.REAL;
import static de.splitstudio.androidb.SqlType.TEXT;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.database.Cursor;

/**
 * Maps {@link java.lang.reflect.Field}'s type to SQL types and vice versa.
 * 
 * @author Matthias Brandt
 * @since 2010
 */
@SuppressWarnings("unchecked")
public class TypeMapper {

	private static Map<SqlType, Set<Class<?>>> typeMap = new HashMap<SqlType, Set<Class<?>>>();
	static {
		//@formatter:off
		typeMap.put(INTEGER, new HashSet<Class<?>>(asList(
			Long.class, 
			long.class, 
			Integer.class, 
			int.class,
			Short.class, 
			short.class, 
			byte.class)));
		typeMap.put(REAL, new HashSet<Class<?>>(asList(
			Float.class, 
			float.class, 
			Double.class, 
			double.class)));
		typeMap.put(TEXT, new HashSet<Class<?>>(asList(
			String.class)));
		//@formatter:on
	}

	public static SqlType getSqlType(final Class<?> type) {
		if (type == null) {
			return NULL;
		}

		for (Entry<SqlType, Set<Class<?>>> entry : typeMap.entrySet()) {
			if (entry.getValue().contains(type)) {
				return entry.getKey();
			}
		}

		return BLOB;
	}

	public static String getValueAsString(final Cursor cursor, final Field field) {
		int index = cursor.getColumnIndex(field.getName());
		Type type = field.getType();
		if (type.equals(long.class) || type.equals(Long.class)) {
			return valueOf(cursor.getLong(index));
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			return valueOf(cursor.getInt(index));
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			return valueOf(cursor.getShort(index));
		} else if (type.equals(byte.class) || type.equals(Byte.class)) {
			return valueOf((byte) cursor.getShort(index));
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return valueOf(cursor.getDouble(index));
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			return valueOf(cursor.getFloat(index));
		} else if (type.equals(char.class)) {
			return valueOf((char) cursor.getShort(index));
		} else if (type.equals(String.class)) {
			return valueOf(cursor.getString(index));
		}
		return "";
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
