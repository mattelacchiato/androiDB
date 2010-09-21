package de.splitstudio.androidb;

import de.splitstudio.androidb.annotation.Column;

public class TableMultipleColumns implements Table {
	public static final String SQL = "CREATE TABLE IF NOT EXISTS TableMultipleColumn ( id INTEGER, text TEXT, amount REAL)";

	@Column
	Integer id;

	@Column
	String text;

	@Column
	float amount;

	@Override
	public boolean isNew() {
		return id == null;
	}
}
