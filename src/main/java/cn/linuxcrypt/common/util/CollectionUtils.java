package cn.linuxcrypt.common.util;


import cn.linuxcrypt.common.Constant;

import java.util.*;

/**
 * @author clibing
 */
public final class CollectionUtils {

    public static Set emptySet() {
        return Constant.Collection.SET_EMPTY;
    }

    public static <T> Set<T> onlySet(T value) {
        Set<T> set = new HashSet<T>(Constant.Number.I.ONE);
        set.add(value);
        return set;
    }

    public static List emptyList() {
        return Constant.Collection.LIST_EMPTY;
    }

    public static <T> List<T> onlyList(T value) {
        List<T> list = new ArrayList<T>(Constant.Number.I.ONE);
        list.add(value);
        return list;
    }

    public static Map emptyMap() {
        return Constant.Collection.MAP_EMPTY;
    }

    public static <K, V> Map<K, V> onlyMap(K key, V value) {
        Map<K, V> map = new HashMap<>(Constant.Number.I.ONE);
        map.put(key, value);
        return map;
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        if (collection == null) {
            return Boolean.TRUE;
        }
        return collection.stream().filter(v -> v != null).count() <= Constant.Number.L.ZERO;
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        if (map == null) {
            return Boolean.TRUE;
        }
        return map.isEmpty();
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public static <E> boolean isEmpty(Set<E> set) {
        if (set == null) {
            return Boolean.TRUE;
        }
        return set.stream().filter(e -> e != null).count() <= Constant.Number.L.ZERO;
    }

    public static <E> boolean isNotEmpty(Set<E> set) {
        return !isEmpty(set);
    }
}
