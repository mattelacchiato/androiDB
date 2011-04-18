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
