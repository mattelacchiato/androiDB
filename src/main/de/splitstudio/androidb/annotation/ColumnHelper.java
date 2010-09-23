package de.splitstudio.androidb.annotation;

import java.lang.reflect.Field;

public class ColumnHelper {

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

}
