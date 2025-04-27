package me.bedwarshurts.mmextension.utils;

import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

public final class InvokeUtils {

    private static final ConcurrentMap<String, Method> cachedMethods = Maps.newConcurrentMap();
    private static final ConcurrentMap<String, Class<?>> cachedClasses = Maps.newConcurrentMap();

    private InvokeUtils() {
        throw new UnsupportedOperationException("You really shouldnt initialise this class");
    }

    public static Object getValue(Class<?> type, String strValue) {
        if (type == double.class) return Double.parseDouble(strValue);
        if (type == float.class) return Float.parseFloat(strValue);
        if (type == int.class) return Integer.parseInt(strValue);
        if (type == long.class) return Long.parseLong(strValue);
        if (type == boolean.class) return Boolean.parseBoolean(strValue);
        if (type == byte.class) return Byte.parseByte(strValue);
        if (type == short.class) return Short.parseShort(strValue);
        if (type == char.class) return strValue.charAt(0);
        if (type.isEnum()) {
            try {
                Method valueOf = type.getMethod("valueOf", String.class);
                return valueOf.invoke(type, strValue);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Not an enum " + e);
            }
        }
        return strValue;
    }

    public static Class<?> getClassFromString(String typeName) throws ClassNotFoundException {
        return cachedClasses.computeIfAbsent(typeName, k -> switch (k) {
            case "byte.class" -> byte.class;
            case "short.class" -> short.class;
            case "int.class" -> int.class;
            case "long.class" -> long.class;
            case "float.class" -> float.class;
            case "double.class" -> double.class;
            case "boolean.class" -> boolean.class;
            case "char.class" -> char.class;
            default -> {
                try {
                    yield Class.forName(k);
                } catch (ClassNotFoundException e) {
                    throw new UnsupportedOperationException("Specified class not found " + e);
                }
            }
        });
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        String key = clazz.getName() + "." + methodName + Arrays.toString(parameterTypes);
        return cachedMethods.computeIfAbsent(key, k -> {
            try {
                if (parameterTypes.length == 0) {
                    return clazz.getMethod(methodName);
                }
                return clazz.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Specified method not found: " + e);
            }
        });
    }

}
