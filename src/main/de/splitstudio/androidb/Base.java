package de.splitstudio.androidb;

import java.lang.reflect.Field;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.ColumnHelper;

public class Base {

	public static final String SQL_UPDATE = "UPDATE %s SET %s WHERE %s";

	public static final String SQL_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s (%s)";

	public static final String SPACE = " ";

	public static final String DELIMITER = ",";

	public static final String QUOTE = "'";

	public static final Object EQUAL = "=";

	private final SQLiteDatabase db;

	public Base(final SQLiteDatabase db) {
		this.db = db;
	}

	public Base(final Context context, final String databaseName) {
		db = DatabaseFactory.create(context, databaseName);
	}

	public boolean insert(final Table table) {
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		if (!ColumnHelper.hasColumns(table) || !createTable(table)) {
			return false;
		}

		try {
			for (Field field : table.getClass().getDeclaredFields()) {
				if (ColumnHelper.isColumn(field)) {
					columns.append(SPACE).append(field.getName()).append(DELIMITER);
					values.append(SPACE).append(QUOTE).append(field.get(table)).append(QUOTE).append(DELIMITER);
				}
			}
			trimLastDelimiter(columns);
			trimLastDelimiter(values);
			db.execSQL(String.format(SQL_INSERT, table.getClass().getSimpleName(), columns, values));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean update(final Table table) {
		Field primaryKey = ColumnHelper.getPrimaryKey(table);
		StringBuilder updateValues = new StringBuilder();

		try {
			if (primaryKey == null || primaryKey.get(table) == null) {
				return false;
			}

			String whereClause = primaryKey.getName() + EQUAL + QUOTE + primaryKey.get(table) + QUOTE;
			for (Field field : table.getClass().getDeclaredFields()) {
				if (ColumnHelper.isColumn(field) && !field.equals(primaryKey)) {
					updateValues.append(field.getName());
					updateValues.append(EQUAL);
					updateValues.append(QUOTE);
					updateValues.append(field.get(table));
					updateValues.append(QUOTE);
					updateValues.append(DELIMITER);
					updateValues.append(SPACE);
				}
			}
			trimLastDelimiter(updateValues);
			db.execSQL(String.format(SQL_UPDATE, table.getClass().getSimpleName(), updateValues, whereClause));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//TODO: do versioning in table!
	boolean createTable(final Table table) {
		String name = table.getClass().getSimpleName();
		StringBuilder sqlColumns = new StringBuilder();

		try {
			for (Field field : table.getClass().getDeclaredFields()) {
				if (ColumnHelper.isColumn(field)) {
					sqlColumns.append(SPACE);
					sqlColumns.append(field.getName());
					sqlColumns.append(SPACE);
					sqlColumns.append(TypeMapper.getSqlType(field.getType()));

					StringBuilder constraints = ColumnHelper.getConstraints(field);
					if (constraints.length() > 0) {
						sqlColumns.append(constraints);
					}
					sqlColumns.append(DELIMITER);
				}
			}
			trimLastDelimiter(sqlColumns);
			db.execSQL(String.format(SQL_CREATE_TABLE, name, sqlColumns));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean fill(final Table table, final Cursor c) {
		if (c.getCount() != 1) {
			return false;
		}

		try {
			for (Field field : table.getClass().getDeclaredFields()) {
				if (ColumnHelper.isColumn(field)) {
					TypeMapper.setTypedValue(c, table, field);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean save(final Table table) {
		if (table.isNew()) {
			return insert(table);
		}
		return update(table);
	}

	private StringBuilder trimLastDelimiter(final StringBuilder sb) {
		return sb.delete(sb.lastIndexOf(DELIMITER), sb.length());
	}

}
