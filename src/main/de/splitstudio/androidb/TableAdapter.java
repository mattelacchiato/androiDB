package de.splitstudio.androidb;

import java.lang.reflect.Field;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import de.splitstudio.androidb.annotation.Column;
import de.splitstudio.androidb.annotation.ColumnHelper;

public class TableAdapter extends CursorAdapter {

	private final Table table;

	private final int viewId;

	public TableAdapter(final Context context, final Cursor cursor, final Table table, final int viewId) {
		super(context, cursor);
		this.table = table;
		this.viewId = viewId;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		for (Field field : table.getFields()) {
			field.setAccessible(true);
			if (ColumnHelper.isColumn(field)) {
				Column column = field.getAnnotation(Column.class);
				if (column != null && column.viewId() >= 0) {
					try {
						String value = TypeMapper.getValueAsString(cursor, field);
						TextView textView = (TextView) view.findViewById(column.viewId());
						textView.setText(value);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(viewId, parent, false);
		bindView(v, context, cursor);
		return v;
	}

}
