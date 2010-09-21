package de.splitstudio.androidb;

public class TableNoColumn implements Table {
	Integer foo;

	@Override
	public boolean isNew() {
		return true;
	}
}
