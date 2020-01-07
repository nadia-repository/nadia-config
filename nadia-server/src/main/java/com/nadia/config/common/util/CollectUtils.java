package com.nadia.config.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author: Wally.Wang
 * @date: 2019/11/25
 * @description:
 */
public class CollectUtils {

    // The consumer must override the equals and hashcode methods of the object being manipulated
    public static <T> List<T> filter(List<T> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Lists.newArrayList();
        }
        Set<T> uniqueSet = Sets.newLinkedHashSet();
        source.forEach(item -> {
            uniqueSet.add(item);
        });
        return CollectionUtils.isEmpty(uniqueSet) ?
            Lists.newArrayList() : Lists.newArrayList(uniqueSet);
    }

    public static <T extends Number> List<T> cleanAndFilter(List<T> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Lists.newArrayList();
        }
        Set<T> uniqueSet = Sets.newLinkedHashSet();
        source.forEach(item -> {
            if (!isNegative(item)) {
                uniqueSet.add(item);
            }
        });
        return CollectionUtils.isEmpty(uniqueSet) ?
            Lists.newArrayList() : Lists.newArrayList(uniqueSet);
    }

    // number <= 0
    private static boolean isNegative(Number number) {
        if (number == null) {
            return true;
        }
        if (number instanceof Long) {
            return number.longValue() <= 0;
        } else if (number instanceof Integer) {
            return number.intValue() <= 0;
        } else if (number instanceof Float) {
            return number.floatValue() <= 0;
        } else if (number instanceof Double) {
            return number.doubleValue() <= 0;
        } else if (number instanceof Byte) {
            return number.byteValue() <= 0;
        } else if (number instanceof Short) {
            return number.shortValue() <= 0;
        }
        return true;
    }

}
