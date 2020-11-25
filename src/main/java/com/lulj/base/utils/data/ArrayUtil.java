package com.lulj.base.utils.data;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 数组操作工具类(基于commons.lang3)
 *
 * @author lu
 * @version 1.0.0
 */
public class ArrayUtil {

    /**
     * 判断数组是否非空
     *
     * @param array
     * @return
     * @author lu
     */
    public static boolean isNotEmpty(Object[] array) {
        return !ArrayUtils.isEmpty(array);
    }

    /**
     * 判断数组是否为空
     *
     * @param array
     * @return
     * @author lu
     */
    public static boolean isEmpty(Object[] array) {
        return ArrayUtils.isEmpty(array);
    }

    /**
     * 连接数组
     *
     * @param array
     * @return
     * @author lu
     */
    public static Object[] concat(Object[] array1, Object[] array2) {
        return ArrayUtils.addAll(array1, array2);
    }

    /**
     * 判断对象是否在数组中
     *
     * @param array
     * @return
     * @author lu
     */
    public static <T> boolean contains(T[] array, T obj) {
        return ArrayUtils.contains(array, obj);
    }

    /**
     * 将数组转换为字符串
     *
     * @param array     待转换的数组
     * @param separator 以什么分隔元素
     * @return
     * @author lu
     */
    public static String toString(Object[] array, String separator) {
        return StringUtils.join(array, separator);
    }

}
