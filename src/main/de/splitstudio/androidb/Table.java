/*
 *    Copyright 2010, Matthias Brandt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import de.splitstudio.androidb.annotation.Column;
import de.splitstudio.androidb.annotation.ColumnHelper;
import de.splitstudio.androidb.annotation.TableMetaData;

/**
 * {@link Table} is the superclass for your database objects. It provides all CRUD Methods {@link #save()},
 * {@link #insert()}, {@link #delete()}, {@link #update()}, {@link #find()} and {@link #all()} to manipulate your object
 * in the db. It holds the {@link #PRIMARY_KEY} to be able to manage all operations.
 * 
 * @author Matthias Brandt
 * @since 2010
 */
public abstract class Table {

	/** String represantation of the primary key. Don't define a field with the same name! */
	public static final String PRIMARY_KEY = "_id";

	/** SQL template for update */
	public static final String SQL_UPDATE = "UPDATE %s SET %s WHERE " + PRIMARY_KEY + "=%s";

	/** SQL template for insert */
	public static final String SQL_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	/** SQL template for table creation */
	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s (%s)";

	private static final String SPACE = " ";

	private static final String DELIMITER = ",";

	private static final String EQUAL = "=";

	/** The database. Normally {@link Table} will create its own instance. */
	protected final SQLiteDatabase db;

	/** Set to remember, which tables were already created. */
	static final Set<String> createdTables = new HashSet<String>();

	private static final String TAG = Table.class.getSimpleName();

	/** The filename for the database. */
	public static final String DB_FILENAME = "androidb.sqlite";

	/**
	 * Welcome! Just provide your context, so we can access the physical database file. We will create or open a new
	 * database file, which is called {@link #DB_FILENAME}.<br/>
	 * At last, it tries to create the table in the database ({@link #createIfNecessary()}), when it can't remember to
	 * have this done yet (see {@link #createdTables}.
	 * 
	 * @param context the context to provide your packagename and path to your app folder on the device.
	 */
	public Table(final Context context) {
		this(context, null);
	}

	/**
	 * See {@link #Table(Context)}. Furthermore, you can provide the {@link #_id} for easier use with {@link #find()} or
	 * {@link #delete()}.
	 * 
	 * @param context the context to provide your packagename and path to your app folder on the device.
	 * @param _id the primary key. You should be careful to set the correct id!
	 */
	public Table(final Context context, final Long _id) {
		//create or open db. Sorry for this ugly stuff, but Java needs the constructor call as first entry.
		this(context.openOrCreateDatabase(DB_FILENAME, SQLiteDatabase.CREATE_IF_NECESSARY, null));
		this._id = _id;
	}

	/**
	 * Constructor for easier testing.
	 * 
	 * @param db
	 */
	/* ...able to test*/
	Table(final SQLiteDatabase db) {
		this.db = db;
		createIfNecessary();
		handleUpgrade();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		db.close();
	}

	private void handleUpgrade() {
		if (getClass().equals(Metadata.class)) {
			return;
		}

		int newVersion = getVersion();
		Metadata metaTable = new Metadata(db);
		if (metaTable.findByName(getTableName())) {
			onUpgrade(metaTable.getTableVersion(), newVersion);
		} else {
			metaTable.setTable(getTableName());
			metaTable.setTableVersion(newVersion);
			if (!metaTable.save()) {
				throw new IllegalStateException("Could not create metadata for Table " + getTableName());
			}
		}
	}

	/** Teh omni-present primary key. NEVER overwrite this field!!! */
	@Column(primaryKey = true, autoIncrement = true, notNull = true)
	protected Long _id = null;

	/**
	 * Checks if this Object was stored in the database.
	 * 
	 * @return <code>false</code> when it was stored in the db.
	 */
	public final boolean isNew() {
		return _id == null || _id < 0;
	}

	/**
	 * Retrieves the table name. It's always the simple name of your implementing class.
	 * 
	 * @return the table name.
	 */
	public final String getTableName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Queries over all rows of this Table.
	 * 
	 * @return the Cursor of this db operation. When no rows were selected, the cursor is empty.
	 */
	public Cursor all() {
		return db.query(getTableName(), getColumnNames(), null, null, null, null, null);
	}

	/**
	 * Find a specific row in the table by it's primary key and fills this object. So, _id has to be set before you call
	 * this.
	 * 
	 * @return <code>true</code>, when a single row was found and filled in this object.
	 */
	public boolean find() {
		try {
			if (_id == null || _id < 0) {
				return false;
			}
			Cursor cursor = db.query(getTableName(), getColumnNames(), PRIMARY_KEY + EQUAL + _id, null, null, null,
				null);
			return fillFirst(cursor);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean find(final Long id) {
		_id = id;
		return find();
	}

	/**
	 * Create a new row in the table and insert all values from this object.
	 * 
	 * @return <code>true</code> when we could save it successfully in the db.
	 */
	public boolean insert() {
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();

		try {
			for (Field field : getFields()) {
				if (ColumnHelper.isColumn(field)) {
					columns.append(SPACE).append(field.getName()).append(DELIMITER);
					values.append(SPACE).append(getValueQuotedIfNeeded(field)).append(DELIMITER);
				}
			}
			trimLastDelimiter(columns);
			trimLastDelimiter(values);
			SQLiteStatement statement = db.compileStatement(String.format(SQL_INSERT, getTableName(), columns, values));
			Long id = statement.executeInsert();
			if (id >= 0) {
				_id = id;
				return true;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return false;
	}

	/**
	 * Delete this object from the db. Though, _id has to be set to find the wanted row.
	 * 
	 * @return <code>true</code> when deletion was successful.
	 */
	public boolean delete() {
		try {
			if (_id == null) {
				return false;
			}
			return db.delete(getTableName(), PRIMARY_KEY + EQUAL + _id, null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Update all values from this object in the db. Though, _id has to be set. Normally, when you call
	 * {@link #update()}, you won't set this yourself. We would suggest to retrieve it via {@link #find()} and than
	 * change the values you want.
	 * 
	 * @return <code>true</code>, when updating was successful.
	 */
	public boolean update() {
		StringBuilder updateValues = new StringBuilder();

		try {
			if (_id == null) {
				return false;
			}

			for (Field field : getFields()) {
				if (ColumnHelper.isColumn(field) && !isPrimaryKey(field)) {
					updateValues.append(field.getName());
					updateValues.append(EQUAL);
					updateValues.append(getValueQuotedIfNeeded(field));
					updateValues.append(DELIMITER);
					updateValues.append(SPACE);
				}
			}
			trimLastDelimiter(updateValues);
			db.execSQL(String.format(SQL_UPDATE, getTableName(), updateValues, _id));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * {@link #insert()} or {@link #update()}.
	 * 
	 * @return <code>true</code> when operation was successful.
	 */
	public boolean save() {
		if (isNew()) {
			return insert();
		}
		return update();
	}

	/**
	 * DROP TABLE IF EXISTS.
	 */
	public void drop() {
		db.execSQL("DROP TABLE IF EXISTS " + getTableName());
		createdTables.remove(getTableName());
	}

	/**
	 * Get the quoted value, when it's a String. Otherwise, the retrieved object will returned as it is.
	 * 
	 * @param field you want to access.
	 * @return the quoted value, when it's a String. Otherwise, the retrieved object will returned as it is.
	 */
	private Object getValueQuotedIfNeeded(final Field field) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		Object value = field.get(this);

		if (value == null) {
			return value;
		}
		if (value instanceof String) {
			return String.format("'%s'", value);
		}
		return value;
	}

	private boolean isPrimaryKey(final Field field) {
		Column column = field.getAnnotation(Column.class);
		return column != null && column.primaryKey();
	}

	/**
	 * When sb has a {@link #DELIMITER}, all chars from there to end will be deleted.
	 * 
	 * @param sb the StringBuffer to trim
	 * @return the trimmed sb.
	 */
	private StringBuilder trimLastDelimiter(final StringBuilder sb) {
		if (sb.lastIndexOf(DELIMITER) >= 0) {
			return sb.delete(sb.lastIndexOf(DELIMITER), sb.length());
		}
		return sb;
	}

	private List<String> getColumnNamesAsList() {
		List<String> columns = new ArrayList<String>();
		for (Field field : getFields()) {
			if (ColumnHelper.isColumn(field)) {
				columns.add(field.getName());
			}
		}
		return columns;
	}

	String[] getColumnNames() {
		List<String> columns = getColumnNamesAsList();
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

	/**
	 * Get the primary key.
	 * 
	 * @return the of the row representing this object. <code>null</code>, when the object wasn't saved in the db, yet.
	 */
	public final Long getId() {
		return _id;
	}

	final void setId(final Long id) {
		this._id = id;
	}

	/**
	 * Fills the first entry the cursor into this object.
	 * 
	 * @param c the cursor containing all column's values.
	 * @return <code>true</code>, when filling was successful.
	 */
	boolean fillFirst(final Cursor c) {
		if (!c.moveToFirst()) {
			return false;
		}

		try {
			for (Field field : getFields()) {
				if (ColumnHelper.isColumn(field)) {
					setTypedValue(c, field);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Executes a {@link #SQL_CREATE_TABLE}, when we can't remember to have this done yet (see {@link #createdTables}.
	 * Afterwards, this table will be added to {@link #createdTables}.
	 * 
	 * @return <code>true</code>, when it was already created, or it the {@link #SQL_CREATE_TABLE} execution was
	 *         successful.
	 */
	boolean createIfNecessary() {
		StringBuilder sqlColumns = new StringBuilder();
		String name = getTableName();

		if (createdTables.contains(name)) {
			return true;
		}

		try {
			for (Field field : getFields()) {
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
			String sql = String.format(SQL_CREATE_TABLE, name, sqlColumns.toString());
			Log.i(TAG, "Executing sql: " + sql);
			db.execSQL(sql);
			createdTables.add(name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	/**
	 * You have to overwrite this method to fullfill your own upgrade handling. This method simply calls {@link #drop()}
	 * . Maybe you want to handle quirks upgrades (when toVersion &lt; fromVersion)...
	 * 
	 * @param fromVersion Version of this table in DB.
	 * @param toVersion Version of this table in current annotation {@link TableMetaData}.
	 */
	protected void onUpgrade(final int fromVersion, final int toVersion) {
		drop();
	}

	private void setTypedValue(final Cursor c, final Field field) throws IllegalArgumentException,
			IllegalAccessException {
		field.setAccessible(true);
		field.set(this, TypeMapper.getTypedValue(c, field));
	}

	public final int getVersion() {
		TableMetaData metaData = getClass().getAnnotation(TableMetaData.class);
		if (metaData == null) {
			throw new IllegalStateException("Table " + getTableName() + " has to declare a version!");
		}
		if (metaData.version() < 1) {
			throw new IllegalStateException("Tableversion of " + getTableName() + " has to be >=1 !");
		}

		return metaData.version();
	}

}
