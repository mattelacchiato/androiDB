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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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

	/** SQL template for index creation */
	public static final String SQL_CREATE_INDEX = "CREATE INDEX IF NOT EXISTS %s ON %s (%s)";

	private static final String SPACE = " ";

	private static final String DELIMITER = ",";

	private static final String EQUAL = "=";

	/** The database. Normally {@link Table} will create its own instance. */
	protected static SQLiteDatabase db = null;

	/** Set to remember, which tables were already created. */
	static final Set<String> createdTables = new HashSet<String>();

	protected static final String TAG = Table.class.getSimpleName();

	/** The suffix for db files. */
	public static final String DB_SUFFIX = ".sqlite";

	/** The filename for the database. */
	public static final String DB_FILENAME = "androidb" + DB_SUFFIX;

	/**
	 * Welcome! Just provide your context, so we can access the physical database file. We will create or open a new
	 * database file, which is called {@link #DB_FILENAME}.<br/>
	 * At last, it tries to create the table in the database ({@link #createIfNecessary()}), when it can't remember to
	 * have this done yet (see {@link #createdTables}.
	 * 
	 * @param context the context to provide your packagename and path to your app folder on the device.
	 */
	public Table() {
		this(null);
	}

	/**
	 * See {@link #Table(Context)}. Furthermore, you can provide the {@link #_id} for easier use with {@link #find()} or
	 * {@link #delete()}.
	 * 
	 * @param context the context to provide your packagename and path to your app folder on the device.
	 * @param _id the primary key. You should be careful to set the correct id!
	 */
	public Table(final Long _id) {
		//create or open db. Sorry for this ugly stuff, but Java needs the constructor call as first entry.
		this._id = _id;
		createIfNecessary();
		handleUpgrade();
	}

	public static SQLiteDatabase openOrCreateDB(final Context context) {
		if (db == null || !db.isOpen()) {
			db = context.openOrCreateDatabase(DB_FILENAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		}
		return db;
	}

	private void handleUpgrade() {
		//Metadata itself is unversioned.
		if (getClass().equals(Metadata.class)) {
			return;
		}

		int newVersion = getVersion();
		Metadata metaTable = new Metadata();
		if (metaTable.findByName(getTableName())) {
			int oldVersion = metaTable.getTableVersion();
			if (oldVersion != newVersion) {
				onUpgrade(oldVersion, newVersion);
			}
		} else {
			metaTable.setTable(getTableName());
		}
		metaTable.setTableVersion(newVersion);
		if (!metaTable.save()) {
			throw new IllegalStateException("Could not save metadata for Table " + getTableName());
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
		return _id == null || _id < 1L;
	}

	/**
	 * Retrieves the table name. It's always the simple name of your implementing class.
	 * 
	 * @return the table name.
	 */
	public final String getTableName() {
		return getTableName(getClass());
	}

	/**
	 * Retrieves the table name. It's always the simple name of your implementing class.
	 * 
	 * @return the table name.
	 */
	public final static String getTableName(final Class<? extends Table> klaas) {
		return klaas.getSimpleName();
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
		return find(_id);
	}

	public boolean find(final Long id) {
		try {
			if (id == null || id < 1L) {
				return false;
			}
			Cursor cursor = db
					.query(getTableName(), getColumnNames(), PRIMARY_KEY + EQUAL + id, null, null, null, null);
			return fillFirstAndClose(cursor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a new row in the table and insert all values from this object. In most cases you want to call
	 * {@link #save()} instead...
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
					values.append(SPACE).append(getEscapedValueQuotedIfNeeded(field)).append(DELIMITER);
				}
			}
			trimLastDelimiter(columns);
			trimLastDelimiter(values);
			String sql = String.format(SQL_INSERT, getTableName(), columns, values);
			Log.i(TAG, "Execute insert: " + sql);
			SQLiteStatement statement = db.compileStatement(sql);
			Long id = statement.executeInsert();
			if (id >= 0L) {
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
		return delete(PRIMARY_KEY + EQUAL + _id);
	}

	public boolean delete(final String whereClause) {
		try {
			if (_id == null) {
				return false;
			}
			return db.delete(getTableName(), whereClause, null) > 0;
		} catch (Exception e) {
			throw new RuntimeException(e);
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

			fieldsToUpdateSql(updateValues);
			trimLastDelimiter(updateValues);
			execSQL(String.format(SQL_UPDATE, getTableName(), updateValues, _id));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	private StringBuilder fieldsToUpdateSql(final StringBuilder updateValues) throws IllegalAccessException {
		for (Field field : getFields()) {
			if (ColumnHelper.isColumn(field) && !isPrimaryKey(field)) {
				updateValues.append(field.getName());
				updateValues.append(EQUAL);
				updateValues.append(getEscapedValueQuotedIfNeeded(field));
				updateValues.append(DELIMITER);
				updateValues.append(SPACE);
			}
		}
		return updateValues;
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
	 * DROP TABLE IF EXISTS, delete from {@link #createdTables} and from {@link Metadata}.
	 */
	public void drop() {
		execSQL("DROP TABLE IF EXISTS " + getTableName());
		createdTables.remove(getTableName());
		Metadata metadata = new Metadata();
		if (metadata.findByName(getTableName())) {
			metadata.delete();
		}
	}

	/**
	 * Get the quoted value, when it's a String. Otherwise, the retrieved object will returned as it is. It will allways
	 * escape the value!
	 * 
	 * @param field you want to access.
	 * @return the quoted value, when it's a String. Otherwise, the retrieved object will returned as it is.
	 */
	private Object getEscapedValueQuotedIfNeeded(final Field field) throws IllegalArgumentException,
			IllegalAccessException {
		field.setAccessible(true);
		Object value = field.get(this);
		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			return DatabaseUtils.sqlEscapeString((String) value);
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

	protected static List<String> getColumnNamesAsList(final Class<? extends Table> klaas) {
		List<String> columns = new ArrayList<String>();
		for (Field field : getFields(klaas)) {
			if (ColumnHelper.isColumn(field)) {
				columns.add(field.getName());
			}
		}
		return columns;
	}

	protected String[] getColumnNames() {
		return getColumnNames(getClass());
	}

	protected static String[] getColumnNames(final Class<? extends Table> klaas) {
		List<String> columns = getColumnNamesAsList(klaas);
		return columns.toArray(new String[columns.size()]);
	}

	/**
	 * @see #getFields(Class)
	 */
	protected List<Field> getFields() {
		return getFields(getClass());
	}

	/**
	 * @return All declared fields from the current class plus the {@link #PRIMARY_KEY} field from {@link Table}.
	 */
	//TODO: get all fields from superclasses to provide inheritance.
	protected static List<Field> getFields(final Class<? extends Table> klaas) {
		List<Field> fields = new ArrayList<Field>();
		try {
			fields.add(Table.class.getDeclaredField(PRIMARY_KEY));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		fields.addAll(Arrays.asList(klaas.getDeclaredFields()));
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

	public final Table setId(final Long _id) {
		this._id = _id;
		return this;
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	/**
	 * Fills the first entry the cursor into this object. Finally, the cursor gets closed.
	 * 
	 * @param c the cursor containing all column's values.
	 * @return <code>true</code>, when filling was successful.
	 */
	protected boolean fillFirstAndClose(final Cursor c) {
		if (!c.moveToFirst()) {
			return false;
		}

		try {
			fill(c);
		} finally {
			c.close();
		}
		return true;
	}

	public boolean fill(final Cursor c) {
		if (c.isAfterLast() || c.isBeforeFirst() && !c.moveToFirst()) {
			return false;
		}

		try {
			for (Field field : getFields()) {
				if (c.getColumnIndex(field.getName()) >= 0) {
					setTypedValue(c, field);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	/**
	 * Fills all entries from the cursor c (beginning with the *next* entry) in an ArrayList with Type T.
	 * 
	 * @param <T> Type for all list entries.
	 * @param klaas Class to instantiate T.
	 * @param c cursor, has to be moved *before* the first entry.
	 * @return a filled ArrayList with T instances, filled with all cursor values.
	 */
	public static <T extends Table> List<T> fillAll(final Class<T> klaas, final Cursor c) {
		ArrayList<T> list = new ArrayList<T>();
		Constructor<T> constructor;
		try {
			constructor = klaas.getConstructor();
			constructor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Could not find constructor " + klaas.getName() + "()", e);
		}

		try {
			while (c.moveToNext()) {
				T table = constructor.newInstance();
				if (table.fill(c)) {
					list.add(table);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("This should not happen. Could not instantiate claas " + klaas, e);
		}
		c.close();
		return list;
	}

	/**
	 * Create this table and all indices, when we can't remember to have this done yet (see {@link #createdTables}.
	 * Afterwards, this table will be added to {@link #createdTables}
	 * 
	 * @return <code>true</code>, when it was already created, or it the {@link #SQL_CREATE_TABLE} execution was
	 *         successful.
	 */
	private void createIfNecessary() {
		StringBuilder sqlColumns = new StringBuilder();
		String name = getTableName();

		if (createdTables.contains(name)) {
			return;
		}

		try {
			for (Field field : getFields()) {
				if (ColumnHelper.isColumn(field)) {
					appendCreateColumns(sqlColumns, field);
				}
			}
			trimLastDelimiter(sqlColumns);

			execSQL(String.format(SQL_CREATE_TABLE, name, sqlColumns.toString()));
			createIndicesIfNecessary();
			createdTables.add(name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createIndicesIfNecessary() {
		HashMap<String, StringBuilder> indices = new HashMap<String, StringBuilder>();
		//fill map
		for (Field field : getFields()) {
			if (ColumnHelper.isColumn(field)) {
				String[] indexNames = field.getAnnotation(Column.class).indexNames();
				for (String indexName : indexNames) {
					if (!indices.containsKey(indexName)) {
						indices.put(indexName, new StringBuilder());
					}
					indices.get(indexName).append(field.getName()).append(DELIMITER);
				}
			}
		}

		//use map
		for (Entry<String, StringBuilder> index : indices.entrySet()) {
			String indexName = index.getKey();
			StringBuilder columns = index.getValue();
			trimLastDelimiter(columns);

			execSQL(String.format(SQL_CREATE_INDEX, indexName, getTableName(), columns.toString()));
		}
	}

	private void appendCreateColumns(final StringBuilder sqlColumns, final Field field) {
		sqlColumns.append(SPACE).append(field.getName()).append(SPACE);
		sqlColumns.append(TypeMapper.getSqlType(field.getType()));

		StringBuilder constraints = ColumnHelper.getConstraints(field);
		if (constraints.length() > 0) {
			sqlColumns.append(constraints);
		}
		sqlColumns.append(DELIMITER);
	}

	/**
	 * You have to overwrite this method to fulfill your own upgrade handling. This method simply calls {@link #drop()}
	 * . Maybe you want to handle quirks upgrades (when toVersion &lt; fromVersion). Then default behavior is drop and
	 * create.
	 * 
	 * @param fromVersion Version of this table in DB.
	 * @param toVersion Version of this table in current annotation {@link TableMetaData}.
	 */
	protected void onUpgrade(final int fromVersion, final int toVersion) {
		drop();
		createIfNecessary();
	}

	private void setTypedValue(final Cursor c, final Field field) throws IllegalArgumentException,
			IllegalAccessException {
		field.setAccessible(true);
		field.set(this, TypeMapper.getTypedValue(c, field));
	}

	/**
	 * Get the annotated version of this table.
	 * 
	 * @return the annotated version of this table.
	 */
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

	private void execSQL(final String sql) {
		Log.i(TAG, "Executing sql: " + sql);
		db.execSQL(sql);
	}

	@Override
	public String toString() {
		try {
			return fieldsToUpdateSql(new StringBuilder()).toString();
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * A table is equal to another table, when both have the same columns and values in columns.
	 * 
	 * @param obj other table to check.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Table) || getClass() != obj.getClass()) {
			return false;
		}
		Table other = (Table) obj;
		List<Field> fields = getFields();
		if (!Arrays.equals(fields.toArray(), other.getFields().toArray())) {
			return false;
		}

		try {
			for (Field field : fields) {
				if (!field.get(this).equals(field.get(other))) {
					return false;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	protected void finalize() throws Throwable {
		if (db != null && db.isOpen()) {
			Log.w(
				TAG,
				String.format(
					"Finalizing Table object %s, but DB is still open. This is no problem until you call closeDB() for your own. Take care to avoid memory leaks!",
					getTableName()));
		}
		super.finalize();
	}

	/**
	 * Closes the DB instance. You have to close the DB for your own, it won't get called on {@link #finalize()}!
	 */
	public static void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	public static void setDb(final SQLiteDatabase newDB) {
		db = newDB;
	}

}
