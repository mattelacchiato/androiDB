package de.splitstudio.androidb;

import java.lang.reflect.Field;

import android.database.Cursor;

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
		if (type.equals(Integer.class) || type.equals(Short.class) || type.equals(Byte.class)
				|| type.isInstance(Long.class) || type.equals(int.class) || type.equals(short.class)
				|| type.equals(byte.class) || type.equals(long.class)) {
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

	public static void setTypedValue(final Cursor c, final Object obj, final Field field)
			throws IllegalArgumentException, IllegalAccessException {
		int index = c.getColumnIndex(field.getName());
		Class<?> type = field.getType();

		if (type.equals(long.class)) {
			field.setLong(obj, c.getLong(index));
		} else if (type.equals(Long.class)) {
			field.set(obj, c.getLong(index));
		} else if (type.equals(int.class)) {
			field.setInt(obj, c.getInt(index));
		} else if (type.equals(Integer.class)) {
			field.set(obj, c.getInt(index));
		} else if (type.equals(short.class)) {
			field.setShort(obj, c.getShort(index));
		} else if (type.equals(Short.class)) {
			field.set(obj, c.getShort(index));
		} else if (type.equals(byte.class)) {
			field.setByte(obj, (byte) c.getShort(index));
		} else if (type.equals(Byte.class)) {
			field.set(obj, (byte) c.getShort(index));
		} else if (type.equals(double.class)) {
			field.setDouble(obj, c.getDouble(index));
		} else if (type.equals(Double.class)) {
			field.set(obj, c.getDouble(index));
		} else if (type.equals(float.class)) {
			field.setFloat(obj, c.getFloat(index));
		} else if (type.equals(Float.class)) {
			field.set(obj, c.getFloat(index));
		} else if (type.equals(char.class)) {
			field.setChar(obj, (char) c.getShort(index));
		} else if (type.equals(String.class)) {
			field.set(obj, c.getString(index));
		}
	}

}
