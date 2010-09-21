package de.splitstudio.androidb;

import java.lang.annotation.Annotation;

import de.splitstudio.androidb.annotation.Column;

public class TypeMapper {

	public static String getSqlType(final Class<?> type) {
		if (type == null) {
			return "NULL";
		}
		if (type.equals(Integer.class) || type.equals(Short.class) || type.equals(Byte.class)
				|| type.isInstance(Long.class)) {
			return "INTEGER";
		}
		if (type.equals(Float.class) || type.equals(Double.class)) {
			return "REAL";
		}
		if (type.equals(String.class)) {
			return "TEXT";
		}

		return "BLOB";
	}

	public static String getConstraints(final Annotation[] annotations) {
		String constraints = "";
		for (Annotation annotation : annotations) {
			if (annotation instanceof Column) {
				Column column = ((Column) annotation);
				if (column.primaryKey()) {
					constraints += "PRIMARY KEY ";
					if (column.autoIncrement()) {
						constraints += "AUTOINCREMENT ";
					}
				}
				if (column.notNull()) {
					constraints += "NOT NULL ";
				}
			}
		}
		return constraints;
	}
}
