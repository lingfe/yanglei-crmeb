package com.utils;

import java.util.*;

/**
 * ThreadLocalUtil.java 线程工具类
 *  https://www.zhihu.com/question/341005993
 *  https://blog.csdn.net/tianlincao/article/details/6039926
 * @author: 零风
 * @CreateDate: 2021/9/18 15:24
 **/
public final class ThreadLocalUtil {

    private ThreadLocalUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(() -> new HashMap(4));

    public static Map<String, Object> getThreadLocal(){
        return threadLocal.get();
    }

    public static <T> T get(String key) {
        Map map = (Map)threadLocal.get();
        return (T)map.get(key);
    }

    /**
     * 该方法返回当前线程所对应的线程局部变量
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T get(String key,T defaultValue) {
        Map map = (Map)threadLocal.get();
        return (T)map.get(key) == null ? defaultValue : (T)map.get(key);
    }

    /**
     * 设置当前线程的线程局部变量的值(key+value)
     * @param key
     * @param value
     */
    public static void set(String key, Object value) {
        Map map = (Map)threadLocal.get();
        map.put(key, value);
    }

    /**
     * 设置当前线程的线程局部变量的值(map)
     * @param keyValueMap
     */
    public static void set(Map<String, Object> keyValueMap) {
        Map map = (Map)threadLocal.get();
        map.putAll(keyValueMap);
    }

    /**
     * 将当前线程局部变量的值删除，目的是为了减少内存的占用，
     * 该方法是JDK 5.0新增的方法。
     * 需要指出的是，当线程结束后，
     * 对应该线程的局部变量将自动被垃圾回收，
     * 所以显式调用该方法清除线程的局部变量并不是必须的操作，
     * 但它可以加快内存回收的速度。
     */
    public static void remove() {
        threadLocal.remove();
    }

    public static <T> Map<String,T> fetchVarsByPrefix(String prefix) {
        Map<String,T> vars = new HashMap<>();
        if( prefix == null ){
            return vars;
        }
        Map map = (Map)threadLocal.get();
        Set<Map.Entry> set = map.entrySet();

        for( Map.Entry entry : set ){
            Object key = entry.getKey();
            if( key instanceof String ){
                if( ((String) key).startsWith(prefix) ){
                    vars.put((String)key,(T)entry.getValue());
                }
            }
        }
        return vars;
    }

    public static <T> T remove(String key) {
        Map map = (Map)threadLocal.get();
        return (T)map.remove(key);
    }

    public static void clear(String prefix) {
        if( prefix == null ){
            return;
        }
        Map map = (Map)threadLocal.get();
        Set<Map.Entry> set = map.entrySet();
        List<String> removeKeys = new ArrayList<>();

        for( Map.Entry entry : set ){
            Object key = entry.getKey();
            if( key instanceof String ){
                if( ((String) key).startsWith(prefix) ){
                    removeKeys.add((String)key);
                }
            }
        }
        for( String key : removeKeys ){
            map.remove(key);
        }
    }
}
