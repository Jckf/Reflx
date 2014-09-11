package it.flaten.reflx.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class Interceptor implements InvocationHandler {
    public static Object getProxy(Class[] interfaces, Object original, Object custom, Map<String, String> mapping) {
        return Proxy.newProxyInstance(
            Interceptor.class.getClassLoader(),
            interfaces,
            new Interceptor(original, custom, mapping)
        );
    }

    private final Object original;
    private final Object custom;
    private final Map<String, String> mapping;

    private final Map<String, Method> methodCache;

    public Interceptor(Object original, Object custom, Map<String, String> mapping) {
        this.original = original;
        this.custom = custom;
        this.mapping = mapping;

        this.methodCache = new HashMap<String, Method>();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // Create an array and a string of argument types.
        Class[] argClasses = new Class[args.length];
        String argClassesString = "";
        for (int i = 0; i < args.length; i++) {
            argClasses[i] = args[i].getClass();
            argClassesString += argClasses[i].getSimpleName() + ",";
        }

        // A unique identifier for this method.
        String cacheKey = this.original.getClass().getName() + "." + method.getName() + "(" + argClassesString.substring(0, argClassesString.length() - 1) + ")";

        System.out.println("Intercepting " + cacheKey);

        // Look for a custom method to intercept this invokation.
        boolean isCustom = false;
        if (this.methodCache.containsKey(cacheKey)) {
            // Found a method in the cache.
            method = this.methodCache.get(cacheKey);
            isCustom = true;
        } else if (this.mapping.containsKey(cacheKey)) {
            // Look for a compatible method.
            Method customMethod = ReflectionUtils.getCompatibleMethod(this.custom.getClass(), this.mapping.get(cacheKey), argClasses);

            // Maybe the mappings are incorrect. Make sure we have a something.
            if (customMethod != null) {
                method = customMethod;
                this.methodCache.put(cacheKey, customMethod);
                isCustom = true;
            }
        }

        // Go ahead.
        try {
            return method.invoke(isCustom ? this.custom : this.original, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        // Things went south.
        return null;
    }
}
