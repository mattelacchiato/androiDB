package de.splitstudio.androidb;

public class TypeMapper {

	public static String getSqlType(final Object o){
		if (o == null){
			return "NULL";
		}
		if(o instanceof Number){
			if(o instanceof Integer || o instanceof Short|| o instanceof Byte || o instanceof Long){
				return "INTEGER";
			}
			if(o instanceof Float || o instanceof Double){
				return "REAL";
			}
		}
		if(o instanceof String) {
			return "TEXT";
		}

		return "BLOB";
	}

}
