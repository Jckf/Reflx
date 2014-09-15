package it.flaten.reflx.reflection;

import it.flaten.reflxapi.config.Configuration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Mapper {
    private final Configuration config;

    private Map<String, Class> cache;

    public Mapper(String path) throws FileNotFoundException {
        this(new File(path));
    }

    public Mapper(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public Mapper(InputStream stream) {
        this.config = new Configuration();

        this.cache = new HashMap<>();

        this.config.load(stream);
    }

    public Class mapClass(String clazz) {
        if (this.cache.containsKey(clazz))
            return this.cache.get(clazz);

        Class mapped;
        try {
            mapped = Class.forName(this.config.getString("classes." + clazz));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        this.cache.put(clazz, mapped);

        return mapped;
    }
}
