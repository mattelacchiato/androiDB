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

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class TableAdapter<T extends Table> extends BaseAdapter {

	private final List<T> tables;

	public TableAdapter(final List<T> tables) {
		this.tables = tables;
	}

	public int getCount() {
		return tables.size();
	}

	public T getItem(final int index) {
		return tables.get(index);
	}

	public long getItemId(final int index) {
		return getItem(index).getId();
	}

	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View v;
		if (convertView == null) {
			v = newView(tables, parent);
		} else {
			v = convertView;
		}
		bindView(v, tables);
		return v;
	}

	public abstract void bindView(final View v, final List<T> tables);

	public abstract View newView(final List<T> tables, final ViewGroup parent);

}
