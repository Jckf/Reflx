package it.flaten.reflx.reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class MethodCache {
    private static final Map<String, Method> cache = new HashMap<>();

    public static String getKey(Object object, String methodName, Object[] args) {
        String argClassesString = "";
        for (int i = 0; i < args.length; i++)
            argClassesString += args[i].getClass().getSimpleName() + ", ";

        return object.getClass().getName() + "." + methodName + "(" + (argClassesString.length() > 0 ? argClassesString.substring(0, argClassesString.length() - 2) : "") + ")";
    }

    public static Method getMethod(Object object, String methodName, Object[] args) {
        Class[] argClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++)
            argClasses[i] = args[i].getClass();

        String key = getKey(object, methodName, args);

        if (cache.containsKey(key))
            return cache.get(key);

        Method method = ReflectionUtils.getCompatibleMethod(object.getClass(), methodName, argClasses);

        if (method == null)
            return null;

        cache.put(key, method);

        return method;
    }
}
