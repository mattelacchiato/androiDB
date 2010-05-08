package de.splitstudio.androidb;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.database.Cursor;
import de.splitstudio.androidb.annotation.Column;


public abstract class Table  {

	public static boolean fill(final Table table, final Cursor c){
		if (c.getCount()!=1){
			return false;
		}

		Field[] fields = table.getClass().getDeclaredFields();
		try {
			for(int i = 0; i < fields.length;++i){
				Object value = CursorHelper.getTypedValue(c,fields[i]);
				fields[i].set(table, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static String[] getColumns(final Class<? extends Table> tableClass){
		Field[] fields = tableClass.getDeclaredFields();
		ArrayList<String> columns = new ArrayList<String>(fields.length);
		for(int i = 0; i < fields.length; ++i) {
			if (fields[i].isAnnotationPresent(Column.class)){
				columns.add(fields[i].getName());
			}
		}
		return columns.toArray(new String[columns.size()]);
	}
}
