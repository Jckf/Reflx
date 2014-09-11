package it.flaten.reflx.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Composite {
    private final Object parent;

    public Composite(Object parent) {
        this.parent = parent;
    }

    protected Object invoke(String methodName, Object... args) {
        // Fetch the method.
        Method method = MethodCache.getMethod(this.parent, methodName, args);

        // Try to invoke it.
        if (method != null) {
            try {
                return method.invoke(this.parent, args);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Things did not go according to plan.
        return null;
    }
}
