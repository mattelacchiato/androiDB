package de.splitstudio.androidb.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.splitstudio.androidb.Table;

public class ReflectionHelper {

	public static <T> T createInstance(final Class<T> klaas) {
		try {
			return getConstructor(klaas).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("This should not happen. Could not instantiate claas " + klaas, e);
		}
	}

	public static <T> Constructor<T> getConstructor(final Class<T> klaas) {
		Constructor<T> constructor;
		try {
			constructor = klaas.getConstructor();
			constructor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Could not find constructor " + klaas.getName() + "()", e);
		}
		return constructor;
	}

	/**
	 * @return All declared fields from the current class plus the {@link Table#PRIMARY_KEY} field from {@link Table}.
	 */
	//TODO: get all fields from superclasses to provide inheritance.
	public static List<Field> getFields(final Class<? extends Table> klaas) {
		List<Field> fields = new ArrayList<Field>();
		try {
			fields.add(Table.class.getDeclaredField(Table.PRIMARY_KEY));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		fields.addAll(Arrays.asList(klaas.getDeclaredFields()));
		return fields;
	}

}
