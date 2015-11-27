package net.falsework.util;

import java.util.Map;

/**
 * 断言类。
 * 
 */
public class Assert {
	public static void assertNotNull(String name, Object value) {
		if ( value == null ) { 
			throw new NotNullException(name);
		}
	}
	
	public static void assertNotNull(Map<String, ?> map, String name) {
		assertNotNull(name, map.get(name));
	}
}
