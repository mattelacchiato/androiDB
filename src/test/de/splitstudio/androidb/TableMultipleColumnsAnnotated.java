package de.splitstudio.androidb;

import de.splitstudio.androidb.annotation.Column;

public class TableMultipleColumnsAnnotated implements Table {
	public static final String SQL = "CREATE TABLE IF NOT EXISTS "
			+ TableMultipleColumnsAnnotated.class.getSimpleName()
			+ " ( id INTEGER PRIMARY KEY, text TEXT, amount REAL)";

	@Column(primaryKey = true)
	Integer id;

	@Column
	String text;

	@Column
	float amount;

	public boolean isNew() {
		return id == null;
	}
}
