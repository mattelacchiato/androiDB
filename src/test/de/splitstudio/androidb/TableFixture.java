package de.splitstudio.androidb;

import de.splitstudio.androidb.annotation.Column;

public class TableFixture implements Table{

	@Column(autoIncrement=true)
	private Integer id;

}
