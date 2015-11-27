package net.falsework.util;

import java.lang.management.ManagementFactory;

/**
 * 当前主机信息类。
 * 
 * @author sea.bao
 */
public class HostInfo {
	/**
	 * 主机前缀
	 */
	private static String hostPrefix;

	static {
		hostPrefix = getLocalHostName() + ":" + getProcessId() + ":";
	}

	private static int getProcessId() {
		try {
			String name = ManagementFactory.getRuntimeMXBean().getName();
			StringBuffer pid = new StringBuffer();
			for (int i = 0, l = name.length(); i < l; i++) {
				if (Character.isDigit(name.charAt(i))) {
					pid.append(name.charAt(i));
				} else if (pid.length() > 0) {
					break;
				}
			}

			return Integer.parseInt(pid.toString());
		} catch (Throwable e) {
			return 0;
		}
	}

	private static String getLocalHostName() {
		try {
			return java.net.InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return "Unknown";
		}
	}

	public static String getHostInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append(hostPrefix);
		sb.append(Thread.currentThread().getId());
		return sb.toString();
	}
}
