package de.splitstudio.androidb;

import de.splitstudio.androidb.annotation.Column;

public class TableColumnWithAnnotations implements Table {
	public static final String SQL = "CREATE TABLE IF NOT EXISTS TableColumnWithAnnotations ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)";

	@Column(primaryKey = true, autoIncrement = true, notNull = true)
	public Integer id;

	public boolean isNew() {
		return id == null;
	}
}
