package de.splitstudio.androidb;

import java.lang.reflect.Field;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.ColumnHelper;

public class Base {

	private static final String SQL_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s (%s)";

	private static final String SPACE = " ";

	private static final String DELIMITER = ",";

	private final SQLiteDatabase db;

	public Base(final SQLiteDatabase db) {
		this.db = db;
	}

	public Base(final Context context, final String databaseName) {
		db = DatabaseFactory.create(context, databaseName);
	}

	public Cursor getById(final Table table, final long id) {
		return getById(table.getClass(), id);
	}

	public Cursor getById(final Class<? extends Table> klaas, final long id) {
		return db.query(klaas.getSimpleName(), ColumnHelper.getColumns(klaas), "WHERE id = " + id, null, null, null,
			null);
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
					values.append(SPACE).append(field.get(table)).append(DELIMITER);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		columns.deleteCharAt(columns.lastIndexOf(DELIMITER));
		values.deleteCharAt(values.lastIndexOf(DELIMITER));

		try {
			db.execSQL(String.format(SQL_INSERT, table.getClass().getSimpleName(), columns, values));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	boolean createTable(final Table table) {
		String name = table.getClass().getSimpleName();
		StringBuilder sqlColumns = new StringBuilder();

		for (Field field : table.getClass().getDeclaredFields()) {
			String fielName = field.getName();
			if (ColumnHelper.isColumn(field)) {
				try {
					String constraints = TypeMapper.getConstraints(field.getAnnotations());
					sqlColumns.append(SPACE).append(fielName);
					sqlColumns.append(SPACE).append(TypeMapper.getSqlType(field.getType()));
					if (constraints.length() > 0) {
						sqlColumns.append(SPACE).append(constraints);
					}
					sqlColumns.append(DELIMITER);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		//TODO: do versioning in table!
		sqlColumns.deleteCharAt(sqlColumns.lastIndexOf(DELIMITER));
		db.execSQL(String.format(SQL_CREATE_TABLE, name, sqlColumns));
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

	public boolean fillById(final Table table, final long id) {
		return fill(table, getById(table, id));
	}

	public boolean save(final Table table) {
		if (table.isNew()) {
			return insert(table);
		}
		/*TODO: update!*/
		return false;
	}

}
