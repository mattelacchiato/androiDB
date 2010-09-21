package de.splitstudio.androidb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.Column;

public class Base {

	private static final Long ERR_FAIL = new Long(-1);

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
		return db.query(klaas.getSimpleName(), getColumns(klaas), "WHERE id = " + id, null, null, null, null);
	}

	public Long insert(final Table table) {
		if (!createTable(table)) {
			return ERR_FAIL;
		}

		ContentValues values = new ContentValues();
		List<String> columns = Arrays.asList(getColumns(table));

		try {
			for (Field field : table.getClass().getDeclaredFields()) {
				//TODO: Outsource type mapping
				//TODO: use plain sql string instead of contentvalues?!
				String fieldName = field.getName();
				if (columns.contains(fieldName)) {
					Object value = field.get(table);
					if (value instanceof String) {
						values.put(fieldName, (String) value);
					} else if (value instanceof Integer) {
						values.put(fieldName, (Integer) value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ERR_FAIL;
		}

		return db.insert(table.getClass().getSimpleName(), null, values);
	}

	public boolean createTable(final Table table) {
		String name = table.getClass().getSimpleName();
		String sqlColumns = "";
		boolean hasAnnotions = false;

		for (Field field : table.getClass().getDeclaredFields()) {
			String fielName = field.getName();
			if (field.getAnnotation(Column.class) != null) {
				hasAnnotions = true;
				try {
					String constraints = TypeMapper.getConstraints(field.getAnnotations());
					sqlColumns += " " + fielName;
					sqlColumns += " " + TypeMapper.getSqlType(field.getType());
					if (!constraints.isEmpty()) {
						sqlColumns += " " + constraints;
					}
					sqlColumns += ",";
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		if (!hasAnnotions) {
			throw new IllegalArgumentException("Table " + name + " has no Annotions!");
		}

		//rm last comma TODO: cleaner!
		//TODO: do versioning in table!
		sqlColumns = sqlColumns.substring(0, sqlColumns.length() - 1);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + name + " (" + sqlColumns + ") ");
		return true;
	}

	public boolean fill(final Cursor c, final Table table) {
		if (c.getCount() != 1) {
			return false;
		}

		try {
			for (Field field : table.getClass().getDeclaredFields()) {
				Object value = CursorHelper.getTypedValue(c, field);
				field.set(this, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/*TODO: update OR insert logic*/
	public boolean save(final Table table) {
		return insert(table) > 0;
	}

	public String[] getColumns(final Class<? extends Table> klaas) {
		Field[] fields = klaas.getDeclaredFields();
		ArrayList<String> columns = new ArrayList<String>(fields.length);
		for (int i = 0; i < fields.length; ++i) {
			if (fields[i].isAnnotationPresent(Column.class)) {
				columns.add(fields[i].getName());
			}
		}
		return columns.toArray(new String[columns.size()]);
	}

	public String[] getColumns(final Table table) {
		return getColumns(table.getClass());
	}

}
