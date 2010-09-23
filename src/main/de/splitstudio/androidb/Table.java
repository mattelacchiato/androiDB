package de.splitstudio.androidb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.Column;
import de.splitstudio.androidb.annotation.ColumnHelper;

public abstract class Table {

	public static final String PRIMARY_KEY = "_id";

	public static final String SQL_UPDATE = "UPDATE %s SET %s WHERE " + PRIMARY_KEY + "=%s";

	public static final String SQL_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s (%s)";

	public static final String SPACE = " ";

	public static final String DELIMITER = ",";

	public static final String EQUAL = "=";

	private final SQLiteDatabase db;

	static final Set<String> createdTables = new HashSet<String>();

	public Table(final Context context) {
		this(context, null);
	}

	public Table(final Context context, final Long _id) {
		//create or open db.
		this(context.openOrCreateDatabase(context.getPackageName().substring(context.getPackageName().lastIndexOf('.'))
				.concat(".sqlite"), SQLiteDatabase.CREATE_IF_NECESSARY, null));
		this._id = _id;
	}

	/**
	 * Constructor for easier testing.
	 * 
	 * @param db
	 */
	Table(final SQLiteDatabase db) {
		this.db = db;
		createIfNecessary();
	}

	@Column(primaryKey = true, autoIncrement = true, notNull = true)
	protected Long _id = null;

	protected abstract void setValue(Field field, Object value) throws IllegalArgumentException, IllegalAccessException;

	protected abstract Object getValue(Field field) throws IllegalArgumentException, IllegalAccessException;

	public final boolean isNew() {
		return _id == null;
	}

	public String getTableName() {
		return this.getClass().getSimpleName();
	}

	public Cursor all() {
		return db.query(this.getClass().getSimpleName(), getColumns(), null, null, null, null, null);
	}

	public boolean find() {
		try {
			if (_id == null) {
				return false;
			}
			Class<? extends Table> klaas = this.getClass();
			Cursor cursor = db.query(klaas.getSimpleName(), getColumns(), PRIMARY_KEY + EQUAL + _id, null, null, null,
				null);
			return fill(cursor);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean insert() {
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();

		try {
			for (Field field : getFields()) {
				if (isColumn(field)) {
					columns.append(SPACE).append(field.getName()).append(DELIMITER);
					values.append(SPACE).append(getValueQuotedIfNeeded(field)).append(DELIMITER);
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
			if (_id == null) {
				return false;
			}
			return db.delete(this.getClass().getSimpleName(), PRIMARY_KEY + EQUAL + _id, null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean update() {
		StringBuilder updateValues = new StringBuilder();

		try {
			if (_id == null) {
				return false;
			}

			for (Field field : getFields()) {
				if (isColumn(field) && !isPrimaryKey(field)) {
					updateValues.append(field.getName());
					updateValues.append(EQUAL);
					updateValues.append(getValueQuotedIfNeeded(field));
					updateValues.append(DELIMITER);
					updateValues.append(SPACE);
				}
			}
			trimLastDelimiter(updateValues);
			db.execSQL(String.format(SQL_UPDATE, this.getClass().getSimpleName(), updateValues, _id));
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

	private Object getValueQuotedIfNeeded(final Field field) throws IllegalArgumentException, IllegalAccessException {
		Object value = getValue(field);
		if (value == null) {
			return value;
		}
		if (value instanceof String) {
			return String.format("'%s'", value);
		}
		return value;
	}

	private boolean isPrimaryKey(final Field field) {
		return PRIMARY_KEY.equals(field.getName());
	}

	private StringBuilder trimLastDelimiter(final StringBuilder sb) {
		if (sb.lastIndexOf(DELIMITER) >= 0) {
			return sb.delete(sb.lastIndexOf(DELIMITER), sb.length());
		}
		return sb;
	}

	//this method *must* lay in this class to access protected fields!
	private void setTypedValue(final Cursor c, final Field field) throws IllegalArgumentException,
			IllegalAccessException {
		int index = c.getColumnIndex(field.getName());
		Class<?> type = field.getType();

		if (type.equals(long.class) || type.equals(Long.class)) {
			setValue(field, c.getLong(index));
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			setValue(field, c.getInt(index));
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			setValue(field, c.getShort(index));
		} else if (type.equals(byte.class) || type.equals(Byte.class)) {
			setValue(field, (byte) c.getShort(index));
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			setValue(field, c.getDouble(index));
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			setValue(field, c.getFloat(index));
		} else if (type.equals(char.class)) {
			setValue(field, (char) c.getShort(index));
		} else if (type.equals(String.class)) {
			setValue(field, c.getString(index));
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
	//TODO: get all fields from superclasses to provide inheritance.
	List<Field> getFields() {
		List<Field> fields = new ArrayList<Field>();
		try {
			fields.add(Table.class.getDeclaredField(PRIMARY_KEY));
		} catch (Exception e) {}
		fields.addAll(Arrays.asList(this.getClass().getDeclaredFields()));
		return fields;
	}

	public final Long getId() {
		return _id;
	}

	/**
	 * use only for testing!
	 * 
	 * @param id
	 */
	final void setId(final Long id) {
		this._id = id;
	}

	boolean fill(final Cursor c) {
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

	//TODO: do versioning in table!
	boolean createIfNecessary() {
		StringBuilder sqlColumns = new StringBuilder();
		String name = getTableName();

		if (createdTables.contains(name)) {
			return true;
		}

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
			createdTables.add(name);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
