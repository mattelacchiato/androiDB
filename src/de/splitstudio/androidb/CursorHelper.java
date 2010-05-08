package de.splitstudio.androidb;

import java.lang.reflect.Field;

import android.database.Cursor;

public class CursorHelper {

	public static Object getTypedValue(final Cursor c, final Field field) {
		int index = c.getColumnIndex(field.getName());
		if(field.getType().isInstance(Integer.TYPE)) {
			return c.getInt(index);
		}
		return null;
	}

}
