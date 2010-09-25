package de.splitstudio.androidb;

/**
 * Maps {@link java.lang.reflect.Field}'s type to SQL types.
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

}
