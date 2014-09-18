package it.flaten.reflx.reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodCache {
    private static final Map<String, Method> cache = new HashMap<>();

    public static String getKey(Object object, String methodName, Object... args) {
        return getKey(object.getClass(), methodName, args);
    }

    public static String getKey(Class clazz, String methodName, Object... args) {
        String argClassesString = "";
        for (int i = 0; i < args.length; i++)
            argClassesString += (args[i] == null ? "null" : args[i].getClass().getSimpleName()) + ", ";

        return clazz.getName() + "." + methodName + "(" + (argClassesString.length() > 0 ? argClassesString.substring(0, argClassesString.length() - 2) : "") + ")";
    }

    public static Method getMethod(Object object, String methodName, Object... args) {
        return getMethod(object.getClass(), methodName, args);
    }

    public static Method getMethod(Class clazz, String methodName, Object... args) {
        Class[] argClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++)
            argClasses[i] = args[i].getClass();

        String key = getKey(clazz, methodName, args);

        if (cache.containsKey(key))
            return cache.get(key);

        Method method = ReflectionUtils.getCompatibleMethod(clazz, methodName, argClasses);

        if (method == null)
            return null;

        cache.put(key, method);

        return method;
    }
}
