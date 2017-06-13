package com.mindflow.framework.rpc.util;

import java.util.*;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-03-18 22:35
 */
public class ClassUtils {

    /**
     * Maps primitive {@code Class}es to their corresponding wrapper {@code Class}.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();
    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        //primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    /**
     * Maps wrapper {@code Class}es to their corresponding primitive types.
     */
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();
    static {
        for (final Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperMap.entrySet()) {
            final Class<?> primitiveClass = entry.getKey();
            final Class<?> wrapperClass = entry.getValue();
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }

    public static boolean isPrimitiveOrWrapper(final Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    public static boolean isPrimitiveWrapper(final Class<?> type) {
        return wrapperPrimitiveMap.containsKey(type);
    }

    public static Class<?> wrapperToPrimitive(final Class<?> cls) {
        return wrapperPrimitiveMap.get(cls);
    }

    public static Class<?> primitiveToWrapper(final Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }

    public static Class<?> forName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    public static Class<?> forName(String name, ClassLoader cl) throws ClassNotFoundException {
        return Class.forName(name, true, cl);
    }

    public static String getSimpleName(final Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return cls.getSimpleName();
    }

    public static String getPackageName(final Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return cls.getPackage().getName();
    }

    /**
     * 递归返回所有的SupperClasses，包含Object.class
     */
    public static List<Class<?>> getAllSuperClasses(final Class<?> cls) {
        if (cls == null) {
            return null;
        }
        final List<Class<?>> classes = new ArrayList<>();
        Class<?> superclass = cls.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    /**
     * 递归返回本类及所有基类继承的接口，及接口继承的接口。
     */
    public static List<Class<?>> getAllInterfaces(Class<?> cls) {
        LinkedHashSet<Class<?>> interfaces = new LinkedHashSet<>();
        while(cls!=null){
            Class<?>[] arr = cls.getInterfaces();
            if(arr!=null){
                for(Class<?> inter : arr){
                    interfaces.add(inter);
                }
            }
            cls = cls.getSuperclass();
        }
        return new ArrayList<>(interfaces);
    }

    public static ClassLoader getDefaultClassLoader() {
        return getClassLoader(ClassUtils.class);
    }

    /**
     * get class loader
     *
     * @param cls
     * @return class loader
     */
    public static ClassLoader getClassLoader(Class<?> cls) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = cls.getClassLoader();
        }
        return cl;
    }

    /**
     * 兼容原子类型与非原子类型的转换，不考虑依赖两者不同来区分不同函数的场景
     */
    public static void wrapClasses(Class<?>[] source) {
        for (int i = 0; i < source.length; i++) {
            Class<?> wrapClass = wrapperPrimitiveMap.get(source[i]);
            if (wrapClass != null) {
                source[i] = wrapClass;
            }
        }
    }
}
