package de.splitstudio.androidb.annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import de.splitstudio.androidb.TableColumnWithAnnotations;
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

}
