package de.splitstudio.androidb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.Column;
import de.splitstudio.androidb.annotation.ColumnHelper;

public abstract class Table {

	public static final String PRIMARY_KEY = "id";

	public static final String SQL_UPDATE = "UPDATE %s SET %s WHERE id='%s'";

	public static final String SQL_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s (%s)";

	public static final String SPACE = " ";

	public static final String DELIMITER = ",";

	public static final String QUOTE = "'";

	public static final String EQUAL = "=";

	private final SQLiteDatabase db;

	public Table(final SQLiteDatabase db) {
		this.db = db;
	}

	@Column(primaryKey = true, autoIncrement = true, notNull = true)
	protected Long id = null;

	public boolean isNew() {
		return id == null;
	}

	public boolean find() {
		try {
			if (id == null) {
				return false;
			}
			Class<? extends Table> klaas = this.getClass();
			Cursor cursor = db.query(klaas.getSimpleName(), getColumns(), "WHERE id='" + id + "'", null, null, null,
				null);
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
			for (Field field : getFields()) {
				if (isColumn(field)) {
					setTypedValue(c, field);
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
		if (!createTable()) {
			return false;
		}

		try {
			for (Field field : getFields()) {
				if (isColumn(field)) {
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
		try {
			if (id == null) {
				return false;
			}
			return db.delete(this.getClass().getSimpleName(), "WHERE id='" + id + "'", null) > 0;
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
			for (Field field : getFields()) {
				if (isColumn(field)) {
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
		StringBuilder updateValues = new StringBuilder();

		try {
			if (id == null) {
				return false;
			}

			for (Field field : getFields()) {
				if (isColumn(field) && !isPrimaryKey(field)) {
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
			db.execSQL(String.format(SQL_UPDATE, this.getClass().getSimpleName(), updateValues, id));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean isPrimaryKey(final Field field) {
		return PRIMARY_KEY.equals(field.getName());
	}

	public boolean save() {
		if (isNew()) {
			return insert();
		}
		return update();
	}

	private StringBuilder trimLastDelimiter(final StringBuilder sb) {
		if (sb.lastIndexOf(DELIMITER) >= 0) {
			return sb.delete(sb.lastIndexOf(DELIMITER), sb.length());
		}
		return sb;
	}

	private void setTypedValue(final Cursor c, final Field field) throws IllegalArgumentException,
			IllegalAccessException {
		int index = c.getColumnIndex(field.getName());
		Class<?> type = field.getType();

		if (type.equals(long.class) || type.equals(Long.class)) {
			field.set(this, c.getLong(index));
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			field.set(this, c.getInt(index));
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			field.set(this, c.getShort(index));
		} else if (type.equals(byte.class) || type.equals(Byte.class)) {
			field.set(this, (byte) c.getShort(index));
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			field.set(this, c.getDouble(index));
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			field.set(this, c.getFloat(index));
		} else if (type.equals(char.class)) {
			field.set(this, (char) c.getShort(index));
		} else if (type.equals(String.class)) {
			field.set(this, c.getString(index));
		}
	}

	private boolean isColumn(final Field field) {
		return field.getAnnotation(Column.class) != null;
	}

	private List<String> getColumnsAsList() {
		List<String> columns = new ArrayList<String>();
		for (Field field : getFields()) {
			if (isColumn(field)) {
				columns.add(field.getName());
			}
		}
		return columns;
	}

	String[] getColumns() {
		List<String> columns = getColumnsAsList();
		return columns.toArray(new String[columns.size()]);
	}

	/**
	 * @return All declared fields from the current class plus the {@link #PRIMARY_KEY} field from {@link Table}.
	 */
	List<Field> getFields() {
		List<Field> fields = new ArrayList<Field>();
		try {
			fields.add(Table.class.getDeclaredField(PRIMARY_KEY));
		} catch (Exception e) {}
		fields.addAll(Arrays.asList(this.getClass().getDeclaredFields()));
		return fields;
	}

	/**
	 * use only for testing!
	 * 
	 * @param id
	 */
	final void setId(final Long id) {
		this.id = id;
	}

	public final Long getId() {
		return id;
	}

}
