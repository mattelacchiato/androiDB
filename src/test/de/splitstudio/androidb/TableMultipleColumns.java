package de.splitstudio.androidb;

import de.splitstudio.androidb.annotation.Column;

public class TableMultipleColumns implements Table {
	public static final String SQL = "CREATE TABLE IF NOT EXISTS " + TableMultipleColumns.class.getSimpleName()
			+ " ( id INTEGER, text TEXT, amount REAL)";

	@Column
	Integer id;

	@Column
	String text;

	@Column
	float amount;

	public boolean isNew() {
		return id == null;
	}
}
