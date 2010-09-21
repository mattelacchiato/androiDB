package de.splitstudio.androidb.annotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.splitstudio.androidb.Table;

public class ColumnHelper {

	public static boolean isColumn(final Field field) {
		return field.getAnnotation(Column.class) != null;
	}

	public static List<String> getColumnsAsList(final Class<? extends Table> klaas) {
		Field[] fields = klaas.getDeclaredFields();
		ArrayList<String> columns = new ArrayList<String>(fields.length);
		for (int i = 0; i < fields.length; ++i) {
			if (fields[i].isAnnotationPresent(Column.class)) {
				columns.add(fields[i].getName());
			}
		}
		return columns;
	}

	public static String[] getColumns(final Class<? extends Table> klaas) {
		List<String> columns = getColumnsAsList(klaas);
		return columns.toArray(new String[columns.size()]);
	}

	public static String[] getColumns(final Table table) {
		return getColumns(table.getClass());
	}

	public static boolean hasColumns(final Table table) {
		for (Field field : table.getClass().getDeclaredFields()) {
			if (!isColumn(field)) {
				return false;
			}
		}
		return true;
	}

}
