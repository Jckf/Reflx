package it.flaten.reflx.reflection;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class Interceptor implements InvocationHandler, MethodHandler {
    // Native Java proxy.
    public static Object getProxy(Class[] interfaces, Object original, Object custom, Map<String, String> mapping) {
        return Proxy.newProxyInstance(
            Interceptor.class.getClassLoader(),
            interfaces,
            new Interceptor(original, custom, mapping)
        );
    }

    // Javassist proxy.
    public static Object getProxy(Object original, Object custom, Map<String, String> mapping) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(original.getClass());
        return factory.create(
            new Class[]{ },
            new Object[]{ },
            new Interceptor(original, custom, mapping)
        );
    }

    private final Object original;
    private final Object custom;
    private final Map<String, String> mapping;

    public Interceptor(Object original, Object custom, Map<String, String> mapping) {
        this.original = original;
        this.custom = custom;
        this.mapping = mapping;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // Get a string representation of this invokation.
        String key = MethodCache.getKey(this.original, method.getName(), args);

        // Look for a custom handler.
        boolean isCustom = false;
        if (this.mapping.containsKey(key)) {
            Method cached = MethodCache.getMethod(
                this.custom,
                this.mapping.get(key),
                args
            );

            if (cached != null) {
                method = cached;
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

    // Forwards Javassist proxied invokations.
    @Override
    public Object invoke(Object proxy, Method method, Method proceed, Object[] args) throws Throwable {
        return this.invoke(proxy, method, args);
    }
}
