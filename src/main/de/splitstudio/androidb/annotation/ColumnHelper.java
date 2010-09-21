package de.splitstudio.androidb.annotation;

import java.lang.reflect.Field;

public class ColumnHelper {

	public static boolean isColumn(final Field field) {
		return field.getAnnotation(Column.class) != null;
	}

}
