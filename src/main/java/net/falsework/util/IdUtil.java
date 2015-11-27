package net.falsework.util;

/**
 * 身份证工具类
 * 
 * @author alex
 */
public final class IdUtil {
	/**
	 * 身份证每一位的计算分量
	 */
	private static final int[] WEIGHTS = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

	/**
	 * 校验位映射
	 */
	private static final char[] CHECK_VALUES = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };

	/**
	 * 18位身份证格式
	 */
	private static final String ID18_PATTERN = "\\d{17}[0-9xX*]";

	/**
	 * 15位身份证格式
	 */
	private static final String ID15_PATTERN = "\\d{15}";

	/**
	 * 将十五位身份证号转换为十八位
	 * 
	 * @param id15
	 * @return
	 */
	public static String convertId15To18(String id15) {
		if (id15 == null || id15.matches("\\d{15}") == false) {
			throw new IllegalArgumentException("The length of id must be 15, id=" + id15);
		}

		StringBuilder id18 = new StringBuilder();
		id18.append(id15.substring(0, 6));
		id18.append("19");
		id18.append(id15.substring(6, 15));
		id18.append(calcCheckValue(id18.toString()));

		return id18.toString();
	}

	/**
	 * 计算校验位(输入前17位)
	 * 
	 * @param id17
	 * @return
	 */
	public static String calcCheckValue(String id17) {
		if (id17 == null || id17.matches("\\d{17}") == false) {
			throw new IllegalArgumentException("The length of id must be 17, id=" + id17);
		}

		int sum = 0;
		for (int i = 0; i < 17; i++) {
			sum += WEIGHTS[i] * Integer.parseInt(id17.substring(i, i + 1));
		}

		return String.valueOf(CHECK_VALUES[sum % 11]);
	}

	/**
	 * 是否是有效合法身份证
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isValidIdCardNo(String id) {
		if (id == null) {
			return false;
		}

		if (id.matches(ID18_PATTERN)) {
			// 18位身份证 需检查校验位
			String expCv = calcCheckValue(id.substring(0, 17));
			String actCv = id.substring(17, 18).replace('*', 'X'); // *视为X

			return expCv.equalsIgnoreCase(actCv);
		}

		// 15位身份证
		return id.matches(ID15_PATTERN);
	}

	private IdUtil() {
	}
}
