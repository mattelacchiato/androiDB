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

import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.TableMetaData;

@TableMetaData(version = 1)
public class TableColumnWithAnnotations extends Table {

	public static final String SQL = "CREATE TABLE IF NOT EXISTS TableColumnWithAnnotations ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)";

	public TableColumnWithAnnotations(final SQLiteDatabase db) {
		super(db);
	}

}
