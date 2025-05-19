package me.bedwarshurts.mmextension.utils;

import com.google.common.collect.Maps;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ReflectionUtils {

    private static final ConcurrentMap<String, Method> cachedMethods = Maps.newConcurrentMap();
    private static final ConcurrentMap<String, Class<?>> cachedClasses = Maps.newConcurrentMap();

    private ReflectionUtils() {
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

    public static Collection<Class<?>> getAnnotatedClasses(String packageName, Class<? extends Annotation> annotation)
            throws IOException, ClassNotFoundException {
        Collection<Class<?>> result = new HashSet<>();
        String pkgPath = packageName.replace('.', '/') + "/";

        CodeSource src = ReflectionUtils.class
                .getProtectionDomain()
                .getCodeSource();
        if (src == null) return result;

        URL location = src.getLocation();
        JarFile jar;
        if (location.getProtocol().equals("jar")) {
            JarURLConnection conn = (JarURLConnection) location.openConnection();
            jar = conn.getJarFile();
        } else {
            File file = new File(URLDecoder.decode(location.getFile(), StandardCharsets.UTF_8));
            jar = new JarFile(file);
        }

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (name.startsWith(pkgPath) && name.endsWith(".class")) {
                String className = name
                        .substring(0, name.length() - 6)
                        .replace('/', '.');

                Class<?> cls = Class.forName(className);
                if (!cls.isInterface()
                        && !Modifier.isAbstract(cls.getModifiers())
                        && cls.isAnnotationPresent(annotation)) {
                    result.add(cls);
                }
            }
        }

        jar.close();
        return result;
    }
}
