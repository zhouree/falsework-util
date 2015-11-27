package net.falsework.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Bean工具类
 * 
 */
public final class BeanUtil {
	/**
	 * 解析缓存
	 */
	private static final Map<Class<?>, Object> classCache = Collections
			.synchronizedMap(new WeakHashMap<Class<?>, Object>());

	/**
	 * 将Bean转换为Map，排除指定属性集
	 * 
	 * @param obj
	 * @param excludedProps
	 *            为null或空数组则返回所有属性值
	 * @return
	 */
	public static Map<String, Object> asMap(Object obj, String... excludedProps) {
		return asMap(obj, excludedProps, true);
	}

	/**
	 * 将Bean转换为Map，排除指定属性集
	 * 
	 * @param obj
	 * @param excludedProps
	 *            为null或空集合则返回所有属性值
	 * @return
	 */
	public static Map<String, Object> asMap(Object obj, Collection<String> excludedProps) {
		String[] props = (excludedProps != null ? excludedProps.toArray(new String[excludedProps.size()]) : null);
		return asMap(obj, props, true);
	}

	/**
	 * 将Bean转换为Map，包含执行属性集
	 * 
	 * @param obj
	 * @param includedProps
	 *            为null或空数组则返回所有属性值
	 * @return
	 */
	public static Map<String, Object> asMapWithProps(Object obj, String... includedProps) {
		return asMap(obj, includedProps, false);
	}

	/**
	 * 将Bean转换为Map，包含执行属性集
	 * 
	 * @param obj
	 * @param includedProps
	 *            为null或空集合则返回所有属性值
	 * @return
	 */
	public static Map<String, Object> asMapWithProps(Object obj, Collection<String> includedProps) {
		String[] props = (includedProps != null ? includedProps.toArray(new String[includedProps.size()]) : null);
		return asMap(obj, props, false);
	}

	/**
	 * 将Bean转换为Map
	 * 
	 * @param obj
	 * @param props
	 *            配合excluded一同使用,
	 *            如果props为null或空数组，excluded为true则返回所有属性值，excluded为false，则返回空Map
	 * @param excluded
	 *            true:props是被忽略的属性集, false:props是包含的属性集
	 * @return
	 */
	private static Map<String, Object> asMap(Object obj, String[] props, boolean excluded) {
		if (obj == null) {
			return null;
		}

		// result
		Map<String, Object> propMap = new HashMap<String, Object>();

		boolean filter = ArrayUtil.isNotEmpty(props);
		if (filter == false && excluded == false) {
			// include but no props specified
			return propMap;
		}

		Map<String, PropertyDescriptor> propDescMap = getPropDescMap(obj.getClass());
		for (Map.Entry<String, PropertyDescriptor> propDescEntry : propDescMap.entrySet()) {
			if (filter) {
				boolean contains = ArrayUtil.contains(props, propDescEntry.getKey());
				if (contains == excluded) {
					// not included props or is excluded props
					continue;
				}
			}

			try {
				Method readMethod = propDescEntry.getValue().getReadMethod();
				if (readMethod != null) {
					propMap.put(propDescEntry.getKey(), readMethod.invoke(obj));
				}
			} catch (Exception e) {
				throw new RuntimeException("Read property value error, propName=" + propDescEntry.getKey(), e);
			}
		}

		return propMap;
	}

	/**
	 * 将source对象的属性值拷贝至target对象的同名属性中
	 * 
	 * @param source
	 * @param target
	 * @param ignoredProps
	 * @return
	 */
	public static <T> T copyProperties(Object source, T target, String... ignoredProps) {
		return copyProperties(CopyPropModes.STANDARD, source, target, ignoredProps);
	}

	/**
	 * 将source对象的属性值拷贝至target对象的同名属性中
	 * 
	 * @param mode
	 *            模式: 1-source对象属性值不为null，2-target对象属性值未设置，其他-所有均拷贝
	 * @param source
	 * @param target
	 * @param ignoredProps
	 * @return
	 * @see net.falsework.util.BeanUtil.CopyPropModes
	 */
	public static <T> T copyProperties(int mode, Object source, T target, String... ignoredProps) {
		if (source == null || target == null) {
			return target;
		}

		Map<String, PropertyDescriptor> srcPropDescMap = getPropDescMap(source.getClass());
		Map<String, PropertyDescriptor> targetPropDescMap = getPropDescMap(target.getClass());
		for (Map.Entry<String, PropertyDescriptor> srcPropDescEntry : srcPropDescMap.entrySet()) {
			if (ArrayUtil.contains(ignoredProps, srcPropDescEntry.getKey())) {
				continue;
			}

			Method readMethod = srcPropDescEntry.getValue().getReadMethod();
			if (readMethod == null) {
				continue;
			}

			PropertyDescriptor targetPropDesc = targetPropDescMap.get(srcPropDescEntry.getKey());
			if (targetPropDesc == null || targetPropDesc.getWriteMethod() == null) {
				continue;
			}

			try {
				boolean doSet = true;
				Object srcValue = readMethod.invoke(source);

				switch (mode) {
					case CopyPropModes.SOURCE_VALUE_NOT_NULL:
						doSet = (srcValue != null);
						break;

					case CopyPropModes.TARGET_VALUE_NOT_SET:
						Method targetReadMethod = targetPropDesc.getReadMethod();
						if (targetReadMethod != null) {
							Object targetValue = targetReadMethod.invoke(target);
							doSet = (targetValue == null || targetValue.equals(ObjectUtil.defaultValue(targetPropDesc
									.getPropertyType())));
						}
						break;
				}

				if (doSet) {
					targetPropDesc.getWriteMethod().invoke(target, srcValue);
				}
			} catch (Exception e) {
				throw new RuntimeException("Write property value error, propName=" + srcPropDescEntry.getKey(), e);
			}
		}

		return target;
	}

	/**
	 * 将Map的属性值拷贝至target对象的同名属性中
	 * 
	 * @param source
	 * @param target
	 * @param ignoredProps
	 * @return
	 */
	public static <T> T copyProperties(Map<String, Object> source, T target, String... ignoredProps) {
		return copyProperties(CopyPropModes.STANDARD, source, target, ignoredProps);
	}

	/**
	 * 将Map的属性值拷贝至target对象的同名属性中
	 * 
	 * @param mode
	 *            模式: 1-source对象属性值不为null，2-target对象属性值未设置，其他-所有均拷贝
	 * @param source
	 * @param target
	 * @param ignoredProps
	 * @return
	 * @see net.falsework.util.BeanUtil.CopyPropModes
	 */
	public static <T> T copyProperties(int mode, Map<String, Object> source, T target, String... ignoredProps) {
		if (MapUtil.isEmpty(source) || target == null) {
			return target;
		}

		Map<String, PropertyDescriptor> propDescMap = getPropDescMap(target.getClass());
		for (Map.Entry<String, Object> dataEntry : source.entrySet()) {
			if (ArrayUtil.contains(ignoredProps, dataEntry.getKey())) {
				continue;
			}

			PropertyDescriptor propDesc = propDescMap.get(dataEntry.getKey());
			if (propDesc == null || propDesc.getWriteMethod() == null) {
				continue;
			}

			try {
				boolean doSet = true;
				Object srcValue = dataEntry.getValue();

				switch (mode) {
					case CopyPropModes.SOURCE_VALUE_NOT_NULL:
						doSet = (srcValue != null);
						break;

					case CopyPropModes.TARGET_VALUE_NOT_SET:
						Method targetReadMethod = propDesc.getReadMethod();
						if (targetReadMethod != null) {
							Object targetValue = targetReadMethod.invoke(target);
							doSet = (targetValue == null || targetValue.equals(ObjectUtil.defaultValue(propDesc
									.getPropertyType())));
						}

						break;
				}

				if (doSet) {
					propDesc.getWriteMethod().invoke(target, srcValue);
				}
			} catch (Exception e) {
				throw new RuntimeException("Write property value error, propName=" + dataEntry.getKey(), e);
			}
		}

		return target;
	}

	/**
	 * 获得class的propName对应的属性Get方法
	 * 
	 * @param clazz
	 * @param propName
	 * @return
	 */
	public static Method getReadMethod(Class<?> clazz, String propName) {
		if (clazz == null || propName == null) {
			return null;
		}

		PropertyDescriptor propDesc = getPropDescMap(clazz).get(propName);
		return (propDesc != null ? propDesc.getReadMethod() : null);
	}

	/**
	 * 获得class的propName对应的属性Set方法
	 * 
	 * @param clazz
	 * @param propName
	 * @return
	 */
	public static Method getWriteMethod(Class<?> clazz, String propName) {
		if (clazz == null || propName == null) {
			return null;
		}

		PropertyDescriptor propDesc = getPropDescMap(clazz).get(propName);
		return (propDesc != null ? propDesc.getWriteMethod() : null);
	}

	/**
	 * 获取属性描述集(class属性默认忽略)
	 * 
	 * @param clazz
	 * @param ignoredProps
	 *            忽略的字段名
	 * @return
	 */
	public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> clazz, String... ignoredProps) {
		if (clazz == null) {
			return null;
		}

		Map<String, PropertyDescriptor> propDescMap = new HashMap<String, PropertyDescriptor>(getPropDescMap(clazz));
		if (ArrayUtil.isNotEmpty(ignoredProps)) {
			for (String ignoredProp : ignoredProps) {
				propDescMap.remove(ignoredProp);
			}
		}

		return propDescMap;
	}

	/**
	 * 获取属性名称集
	 * 
	 * @param clazz
	 * @param ignoredProps
	 *            忽略的字段名
	 * @return
	 */
	public static List<String> getPropertyNames(Class<?> clazz, String... ignoredProps) {
		if (clazz == null) {
			return null;
		}

		Map<String, PropertyDescriptor> propDescMap = getPropDescMap(clazz);

		List<String> propNames = new ArrayList<String>(propDescMap.size());
		for (Map.Entry<String, PropertyDescriptor> entry : propDescMap.entrySet()) {
			if (ArrayUtil.contains(ignoredProps, entry.getKey()) == false) {
				propNames.add(entry.getKey());
			}
		}

		return propNames;
	}

	/**
	 * 获取两个对象指定属性中属性值不同的属性集合，如果两个对象任意一个为null，返回空集合
	 * 
	 * @param obj1
	 * @param obj2
	 * @param propNamesToCompare
	 *            为null或空数组，则比较两对象同名属性；如果指定属性名在两个对象的任意一个中不存在，则被忽略
	 * @return
	 */
	public static List<String> getPropertyNamesWithDiffValue(Object obj1, Object obj2, String... propNamesToCompare) {
		List<String> propsNames = new ArrayList<String>();

		if (obj1 == null || obj2 == null || obj1 == obj2) {
			return propsNames;
		}

		// 打造对象1的属性名与属性表述信息的映射关系
		Map<String, PropertyDescriptor> propDefMap1 = BeanUtil.getPropertyDescriptors(obj1.getClass());

		// 如果对象2与对象1的类型相同，则使用相同的映射关系
		Map<String, PropertyDescriptor> propDefMap2 = propDefMap1;
		if (obj1.getClass() != obj2.getClass()) {
			// 不同则解析
			propDefMap2 = BeanUtil.getPropertyDescriptors(obj2.getClass());
		}

		// 如果未指定属性名，则比较所有属性(选取任何一个对象的属性映射作为参考)
		if (ArrayUtil.isEmpty(propNamesToCompare)) {
			propNamesToCompare = propDefMap1.keySet().toArray(new String[propDefMap1.size()]);
		}

		// 遍历所有属性，比较属性值
		for (String propName : propNamesToCompare) {
			PropertyDescriptor pd1 = propDefMap1.get(propName);
			PropertyDescriptor pd2 = propDefMap2.get(propName);

			if (pd1 == null || pd1.getReadMethod() == null || pd2 == null || pd2.getReadMethod() == null) {
				continue;
			}

			try {
				// 如果不一致则记录
				if (ObjectUtil.isNotEqual(pd1.getReadMethod().invoke(obj1), pd2.getReadMethod().invoke(obj2))) {
					propsNames.add(propName);
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		return propsNames;
	}

	/**
	 * 获取Class的属性定义
	 * 
	 * @param clazz
	 * @return
	 */
	private static Map<String, PropertyDescriptor> getPropDescMap(Class<?> clazz) {
		Map<String, PropertyDescriptor> propDescMap = getPropDescMapFromCache(clazz);
		if (propDescMap != null) {
			return propDescMap;
		}

		propDescMap = new HashMap<String, PropertyDescriptor>();
		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
				if ("class".equals(pd.getName()) == false) {
					propDescMap.put(pd.getName(), pd);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Analyse property desc error, class=" + clazz, e);
		}
		classCache.put(clazz, new WeakReference<Map<String, PropertyDescriptor>>(propDescMap));

		return propDescMap;
	}

	/**
	 * 从缓存中获取属性描述
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, PropertyDescriptor> getPropDescMapFromCache(Class<?> clazz) {
		WeakReference<Map<String, PropertyDescriptor>> propDescRef = (WeakReference<Map<String, PropertyDescriptor>>) classCache
				.get(clazz);

		return (propDescRef != null ? propDescRef.get() : null);
	}

	/**
	 * 获取指定方法对象
	 * 
	 * @param obj
	 * @param methodName
	 * @return
	 */
	public static Method getMethod(Object obj, String methodName) {
		return getMethod(obj, methodName, null);
	}

	/**
	 * 获取指定Method
	 * 
	 * @param obj
	 * @param methodName
	 * @param paraTypes
	 * @return
	 */
	public static Method getMethod(Object obj, String methodName, Class<?>[] paraTypes) {
		if (obj == null || methodName == null) {
			return null;
		}

		Class<?> clazz = null;
		if (obj instanceof Class) {
			clazz = ((Class<?>) obj);
		} else {
			clazz = obj.getClass();
		}

		Method m = null;
		if (paraTypes != null) {
			try {
				m = clazz.getDeclaredMethod(methodName, paraTypes);
			} catch (Exception e) {
			}
		} else {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.getName().equals(methodName)) {
					m = method; // first method by name
					break;
				}
			}
		}

		if (m == null) {
			return getMethod(clazz.getSuperclass(), methodName, paraTypes);
		}

		return m;
	}
	
	/**
	 * 根据方法和对象获取值
	 * 
	 * @param method
	 * @param o
	 * @return
	 */
	public static Object getValue(Method method, Object o) {
		try {
			return method.invoke(o);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}

	/**
	 * 执行obj对象的指定方法，当调用抛出异常时返回null
	 * 
	 * @param obj
	 * @param methodName
	 * @param args
	 * @return
	 */
	public static <T> T invokeMethodQuietly(Object obj, String methodName, Object... args) {
		try {
			return invokeMethod(obj, methodName, args);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 执行obj对象的指定方法，当调用抛出异常时返回null
	 * 
	 * @param obj
	 * @param method
	 * @param args
	 * @return
	 */
	public static <T> T invokeMethodQuietly(Object obj, Method method, Object... args) {
		try {
			return invokeMethod(obj, method, args);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 执行obj对象的指定方法，调用可能会抛出运行时异常，如方法不存在等
	 * 
	 * @param obj
	 * @param methodName
	 * @param args
	 * @return
	 */
	public static <T> T invokeMethod(Object obj, String methodName, Object... args) {
		if (obj == null || methodName == null) {
			return null;
		}

		Method method = getMethod(obj, methodName);
		if (method == null) {
			throw new RuntimeException("No such method, obj=" + obj + ", method=" + methodName);
		}

		return invokeMethod(obj, method, args);
	}

	/**
	 * 执行obj对象的指定方法
	 * 
	 * @param obj
	 * @param method
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Object obj, Method method, Object... args) {
		if (obj == null || method == null) {
			return null;
		}

		if (method.isAccessible() == false) {
			method.setAccessible(true);
		}

		try {
			return (T) method.invoke(obj, args);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());

		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());

		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	private BeanUtil() {
	}

	/**
	 * 属性拷贝模式类
	 * 
	 * @author alex
	 */
	public static final class CopyPropModes {
		/**
		 * 拷贝模式 - 标准全部拷贝
		 */
		public static final int STANDARD = 0;

		/**
		 * 拷贝模式 - source对象属性值不为null
		 */
		public static final int SOURCE_VALUE_NOT_NULL = 1;

		/**
		 * 拷贝模式 - target对象属性值未设置(为对应类型的默认值)
		 */
		public static final int TARGET_VALUE_NOT_SET = 2;

		private CopyPropModes() {
		}
	}
}
