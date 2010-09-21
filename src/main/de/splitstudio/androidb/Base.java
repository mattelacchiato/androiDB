package de.splitstudio.androidb;

import java.lang.reflect.Field;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.ColumnHelper;

public class Base {

	private static final String EMPTY_STRING = "";

	private static final String SPACE = " ";

	private static final char DELIMITER = ',';

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
		String columns = EMPTY_STRING;
		String values = EMPTY_STRING;
		if (!ColumnHelper.hasColumns(table) || !createTable(table)) {
			return false;
		}

		try {
			for (Field field : table.getClass().getDeclaredFields()) {
				if (ColumnHelper.isColumn(field)) {
					columns += SPACE + field.getName() + DELIMITER;
					values += SPACE + field.get(table) + DELIMITER;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		columns = columns.substring(0, columns.lastIndexOf(DELIMITER));
		values = values.substring(0, values.lastIndexOf(DELIMITER));

		try {
			db.execSQL(String.format("INSERT INTO %s (%s) VALUES (%s)", table.getClass().getSimpleName(), columns,
				values));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	boolean createTable(final Table table) {
		String name = table.getClass().getSimpleName();
		String sqlColumns = EMPTY_STRING;

		for (Field field : table.getClass().getDeclaredFields()) {
			String fielName = field.getName();
			if (ColumnHelper.isColumn(field)) {
				try {
					String constraints = TypeMapper.getConstraints(field.getAnnotations());
					sqlColumns += SPACE + fielName;
					sqlColumns += SPACE + TypeMapper.getSqlType(field.getType());
					if (!constraints.isEmpty()) {
						sqlColumns += SPACE + constraints;
					}
					sqlColumns += DELIMITER;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		//TODO: do versioning in table!
		sqlColumns = sqlColumns.substring(0, sqlColumns.lastIndexOf(DELIMITER));
		db.execSQL("CREATE TABLE IF NOT EXISTS " + name + " (" + sqlColumns + ")");
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
