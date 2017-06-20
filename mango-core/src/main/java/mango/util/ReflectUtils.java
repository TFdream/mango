package mango.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ReflectUtils {

    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";
    private static final String IS_PREFIX = "is";

    public static Field getField(Class<?> clazz, String fieldName){
        for (Class<?> searchType = clazz; searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Field field =  searchType.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                //not handler
            }
        }
        return null;
    }

    public static Method getWriteMethod(Class<?> clazz, String propertyName, Class<?> parameterType) {
        String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(propertyName);
        return getAccessibleMethod(clazz, setterMethodName, parameterType);
    }

    /**
     * 按属性名获取前缀为get或is的方法，并设为可访问
     */
    public static Method getReadMethod(Class<?> clazz, String propertyName) {
        String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(propertyName);

        Method method = getAccessibleMethod(clazz, getterMethodName);
        // retry on another name
        if (method == null) {
            getterMethodName = IS_PREFIX + StringUtils.capitalize(propertyName);
            method = getAccessibleMethod(clazz, getterMethodName);
        }
        return method;
    }

    public static Method getAccessibleMethod(final Class<?> clazz, final String methodName,
                                             Class<?>... parameterTypes) {

        // 处理原子类型与对象类型的兼容
        ClassUtils.wrapClasses(parameterTypes);

        for (Class<?> searchType = clazz; searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                        && !method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            } catch (NoSuchMethodException e) {
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    public static Method[] getMethods(final Class<?> cls){
        return cls.getMethods();
    }

    public static Method[] getDeclaredMethods(final Class<?> cls){
        return cls.getDeclaredMethods();
    }

    public static BeanInfo getBeanInfo(final Class<?> cls) throws IntrospectionException {
        return Introspector.getBeanInfo(cls);
    }

}
