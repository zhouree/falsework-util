package net.falsework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

/**
 * Number工具类
 * 
 * @author alex
 */
public final class NumberUtil {
	/**
	 * 获取Byte值, 如果number为null, 则返回0
	 * 
	 * @param number
	 *            Byte
	 * @return 数值
	 */
	public static Byte defaultNumber(Byte number) {
		return defaultNumber(number, Byte.valueOf((byte) 0));
	}

	/**
	 * 获取Short值, 如果number为null, 则返回0
	 * 
	 * @param number
	 *            Short
	 * @return 数值
	 */
	public static Short defaultNumber(Short number) {
		return defaultNumber(number, Short.valueOf((short) 0));
	}

	/**
	 * 获取Integer值, 如果number为null, 则返回0
	 * 
	 * @param number
	 *            Integer
	 * @return 数值
	 */
	public static Integer defaultNumber(Integer number) {
		return defaultNumber(number, Integer.valueOf(0));
	}

	/**
	 * 获取Long值, 如果number为null, 则返回0
	 * 
	 * @param number
	 *            Long
	 * @return 数值
	 */
	public static Long defaultNumber(Long number) {
		return defaultNumber(number, Long.valueOf(0));
	}

	/**
	 * 获取Float值, 如果number为null, 则返回0
	 * 
	 * @param number
	 *            Float
	 * @return 数值
	 */
	public static Float defaultNumber(Float number) {
		return defaultNumber(number, Float.valueOf(0));
	}

	/**
	 * 获取Double值, 如果number为null, 则返回0
	 * 
	 * @param number
	 *            Double
	 * @return 数值
	 */
	public static Double defaultNumber(Double number) {
		return defaultNumber(number, Double.valueOf(0));
	}

	/**
	 * 获取BigDecimal值, 如果number为null, 则返回0
	 * 
	 * @param number
	 *            BigDecimal
	 * @return 数值
	 */
	public static BigDecimal defaultNumber(BigDecimal number) {
		return defaultNumber(number, BigDecimal.ZERO);
	}

	/**
	 * 获取BigInteger值, 如果number为null, 则返回0
	 * 
	 * @param number
	 *            BigInteger
	 * @return 数值
	 */
	public static BigInteger defaultNumber(BigInteger number) {
		return defaultNumber(number, BigInteger.valueOf(0));
	}

	/**
	 * 获取Number值, 如果number为null, 则返回defNumber
	 * 
	 * @param number
	 *            Number
	 * @return 数值
	 */
	public static <T extends Number> T defaultNumber(T number, T defNumber) {
		return (number != null ? number : defNumber);
	}

	/**
	 * 对numbers中所有数字求和
	 * 
	 * @param numbers
	 * @return
	 */
	public static int sum(int[] numbers) {
		int rst = 0;
		if (numbers != null) {
			for (int number : numbers) {
				rst += number;
			}
		}

		return rst;
	}

	/**
	 * 对numbers中所有数字求和
	 * 
	 * @param numbers
	 * @return
	 */
	public static long sum(long[] numbers) {
		long rst = 0;
		if (numbers != null) {
			for (long number : numbers) {
				rst += number;
			}
		}

		return rst;
	}

	/**
	 * 对numbers中所有不为null的数字求和
	 * 
	 * @param numbers
	 * @return
	 */
	public static BigDecimal sum(Number... numbers) {
		BigDecimal rst = BigDecimal.ZERO;
		if (numbers != null) {
			for (Number num : numbers) {
				if (num != null) {
					rst = rst.add(asBigDecimal(num));
				}
			}
		}

		return rst;
	}

	/**
	 * 对numbers中所有不为null的数字求和
	 * 
	 * @param numbers
	 * @return
	 */
	public static BigDecimal sum(Collection<? extends Number> numbers) {
		BigDecimal rst = BigDecimal.ZERO;
		if (numbers != null) {
			for (Number num : numbers) {
				if (num != null) {
					rst = rst.add(asBigDecimal(num));
				}
			}
		}

		return rst;
	}

	/**
	 * 是否是纯数字，可以包含负号
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isDigits(String number) {
		return (number != null && number.matches("-?\\d+"));
	}

	/**
	 * 将字符串转换成长整形
	 * 
	 * @param number
	 * @param defNum
	 * @return
	 */
	public static Long parseLong(String number, Long defNum) {
		try {
			return (number != null ? Long.valueOf(number) : defNum);
		} catch (Exception ex) {
			return defNum;
		}
	}

	/**
	 * 将字符串转换成整形
	 * 
	 * @param number
	 * @param defNum
	 * @return
	 */
	public static Integer parseInteger(String number, Integer defNum) {
		try {
			return (number != null ? Integer.valueOf(number) : defNum);
		} catch (Exception ex) {
			return defNum;
		}
	}

	/**
	 * 将数字转换为Integer
	 * 
	 * @param number
	 * @return
	 */
	public static Integer asInteger(Number number) {
		return asInteger(number, null);
	}

	/**
	 * 将数字转换为Integer
	 * 
	 * @param number
	 * @param defValIfNull
	 * @return
	 */
	public static Integer asInteger(Number number, Integer defValIfNull) {
		if (number == null) {
			return defValIfNull;

		} else if (number instanceof Integer) {
			return (Integer) number;
		}

		return Integer.valueOf(number.intValue());
	}

	/**
	 * 将数字转换为Long
	 * 
	 * @param number
	 * @return
	 */
	public static Long asLong(Number number) {
		return asLong(number, null);
	}

	/**
	 * 将数字转换为Long
	 * 
	 * @param number
	 * @param defValIfNull
	 * @return
	 */
	public static Long asLong(Number number, Long defValIfNull) {
		if (number == null) {
			return defValIfNull;

		} else if (number instanceof Long) {
			return (Long) number;
		}

		return Long.valueOf(number.longValue());
	}

	/**
	 * 将数字转换为BigDecimal
	 * 
	 * @param number
	 * @return
	 */
	public static BigDecimal asBigDecimal(Number number) {
		return asBigDecimal(number, null);
	}

	/**
	 * 将数字转换为BigDecimal
	 * 
	 * @param number
	 * @param defValIfNull
	 * @return
	 */
	public static BigDecimal asBigDecimal(Number number, BigDecimal defValIfNull) {
		if (number == null) {
			return defValIfNull;

		} else if (number instanceof BigDecimal) {
			return (BigDecimal) number;
		}

		return new BigDecimal(number.toString());
	}

	/**
	 * 构造函数
	 */
	private NumberUtil() {
	}
}
