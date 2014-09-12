package it.flaten.reflx;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarLoader {
    private static final URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
    private static final Class<URLClassLoader> sysclass = URLClassLoader.class;
    private static final Class[] params = new Class[]{ URL.class };
    private static Method method;

    public static void load(String path) {
        load(new File(path));
    }

    public static void load(File file) {
        try {
            load(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void load(URL url) {
        try {
            if (method == null) {
                method = sysclass.getDeclaredMethod("addURL", params);
                method.setAccessible(true);
            }

            method.invoke(sysloader, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
