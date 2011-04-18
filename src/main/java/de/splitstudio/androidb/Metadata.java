/*
 *    Copyright 2011, Matthias Brandt

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

import android.database.Cursor;
import de.splitstudio.androidb.annotation.Column;

class Metadata extends Table {

	@Column(notNull = true)
	private int tableVersion;

	@Column(notNull = true)
	private String tableName;

	public Metadata() {
		super();
	}

	public boolean findByName(final String name) {
		Cursor c = getDb().query(getTableName(), getColumnNames(), "tableName='" + name + "'", null, null, null, null);
		return fillFirstAndClose(c);
	}

	public void setVersion(final int version) {
		this.tableVersion = version;
	}

	public int getTableVersion() {
		return tableVersion;
	}

	public Metadata setTable(final String table) {
		this.tableName = table;
		return this;
	}

	public String getTable() {
		return tableName;
	}

	/*..able to test*/
	Metadata setTableVersion(final int i) {
		this.tableVersion = i;
		return this;
	}

}
