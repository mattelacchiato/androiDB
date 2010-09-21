package de.splitstudio.androidb;

import java.lang.reflect.Field;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.ColumnHelper;

public abstract class Table {

	public static final String SQL_UPDATE = "UPDATE %s SET %s WHERE %s";

	public static final String SQL_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s (%s)";

	public static final String SPACE = " ";

	public static final String DELIMITER = ",";

	public static final String QUOTE = "'";

	public static final Object EQUAL = "=";

	private final SQLiteDatabase db;

	public Table(final SQLiteDatabase db) {
		this.db = db;
	}

	public abstract boolean isNew();

	public boolean find() {
		Field primaryKey = ColumnHelper.getPrimaryKey(this);
		try {
			if (primaryKey == null) {
				return false;
			}
			Object value = primaryKey.get(this);
			if (value == null) {
				return false;
			}
			Class<? extends Table> klaas = this.getClass();
			Cursor cursor = db.query(klaas.getSimpleName(), ColumnHelper.getColumns(klaas), String.format(
				"WHERE %s='%s'", primaryKey.getName(), value), null, null, null, null);
			return fill(cursor);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean fill(final Cursor c) {
		if (c.getCount() != 1) {
			return false;
		}

		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				if (ColumnHelper.isColumn(field)) {
					TypeMapper.setTypedValue(c, this, field);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insert() {
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		if (!ColumnHelper.hasColumns(this) || !createTable()) {
			return false;
		}

		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				if (ColumnHelper.isColumn(field)) {
					columns.append(SPACE).append(field.getName()).append(DELIMITER);
					values.append(SPACE).append(QUOTE).append(field.get(this)).append(QUOTE).append(DELIMITER);
				}
			}
			trimLastDelimiter(columns);
			trimLastDelimiter(values);
			db.execSQL(String.format(SQL_INSERT, this.getClass().getSimpleName(), columns, values));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean delete() {
		Field primaryKey = ColumnHelper.getPrimaryKey(this);
		try {
			Object value = primaryKey.get(this);
			if (primaryKey == null || value == null) {
				return false;
			}
			return db.delete(this.getClass().getSimpleName(), String.format("WHERE %s='%s'", primaryKey.getName(),
				value), null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	//TODO: do versioning in table!
	boolean createTable() {
		String name = this.getClass().getSimpleName();
		StringBuilder sqlColumns = new StringBuilder();

		try {
			for (Field field : this.getClass().getDeclaredFields()) {
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

	public boolean update() {
		Field primaryKey = ColumnHelper.getPrimaryKey(this);
		StringBuilder updateValues = new StringBuilder();

		try {
			if (primaryKey == null || primaryKey.get(this) == null) {
				return false;
			}

			String whereClause = primaryKey.getName() + EQUAL + QUOTE + primaryKey.get(this) + QUOTE;
			for (Field field : this.getClass().getDeclaredFields()) {
				if (ColumnHelper.isColumn(field) && !field.equals(primaryKey)) {
					updateValues.append(field.getName());
					updateValues.append(EQUAL);
					updateValues.append(QUOTE);
					updateValues.append(field.get(this));
					updateValues.append(QUOTE);
					updateValues.append(DELIMITER);
					updateValues.append(SPACE);
				}
			}
			trimLastDelimiter(updateValues);
			db.execSQL(String.format(SQL_UPDATE, this.getClass().getSimpleName(), updateValues, whereClause));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean save() {
		if (isNew()) {
			return insert();
		}
		return update();
	}

	private StringBuilder trimLastDelimiter(final StringBuilder sb) {
		return sb.delete(sb.lastIndexOf(DELIMITER), sb.length());
	}

}
