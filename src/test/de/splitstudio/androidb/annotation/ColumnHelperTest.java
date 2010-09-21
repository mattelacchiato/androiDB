package de.splitstudio.androidb.annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.splitstudio.androidb.Table;
import de.splitstudio.androidb.TableColumnWithAnnotations;
import de.splitstudio.androidb.TableMultipleColumns;
import de.splitstudio.androidb.TableNoColumn;

public class ColumnHelperTest {

	@Test
	public void getColumns_returnsAllAnnotatedColumns() {
		assertThat(ColumnHelper.getColumns(TableColumnWithAnnotations.class), equalTo(new String[] { "id" }));
	}

	@Test
	public void getColumns_noAnnotatedField_emptyArray() {
		assertThat(ColumnHelper.getColumns(TableNoColumn.class), equalTo(new String[] {}));
	}

	@Test
	public void hasColumns_TableWithoutColumn_false() {
		assertFalse(ColumnHelper.hasColumns(new TableNoColumn()));
	}

	@Test
	public void hasColumns_TableWithColumns_true() {
		assertTrue(ColumnHelper.hasColumns(new TableMultipleColumns()));
	}

	@Test
	public void getPrimaryKey_TableWithPrimaryKey_primaryKeyField() throws SecurityException, NoSuchFieldException {
		Table table = new TableColumnWithAnnotations();
		assertThat(ColumnHelper.getPrimaryKey(table), equalTo(table.getClass().getField("id")));
	}
}
