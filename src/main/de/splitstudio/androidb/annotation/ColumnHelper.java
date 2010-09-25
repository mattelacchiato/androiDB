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
