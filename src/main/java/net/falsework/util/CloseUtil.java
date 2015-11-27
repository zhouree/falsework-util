package net.falsework.util;

import java.lang.reflect.Method;

/**
 * 关闭工具类。
 * 
 * @author sea.bao
 */
public class CloseUtil {
	public static void close(Object... resList) {
		for (Object res : resList) {
			try {
				if (res == null) {
					continue;
				}

				Method method = res.getClass().getMethod("close");
				method.invoke(res);
			} catch (Throwable e) {
				// ignore close exception.
			}
		}
	}
}
