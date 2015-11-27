package net.falsework.util;

/**
 * 系统工具类。
 * 
 */
public class SystemUtil {
	public static boolean isWindows() {
		String os = System.getProperty("os.name");
		if ( os.toUpperCase().indexOf("WINDOWS") >=0 ) {
			return true;
		} else {
			return false;
		}
	}
}
