package it.flaten.reflxapi.config;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class Configuration extends ConfigurationSection {
    private final Yaml yaml;

    public Configuration() {
        super();

        this.yaml = new Yaml();
    }

    public void load(String path) throws FileNotFoundException {
        this.load(new File(path));
    }

    public void load(File file) throws FileNotFoundException {
        this.load(new FileInputStream(file));
    }

    public void load(InputStream stream) {
        this.setData((Map<String, Object>) this.yaml.load(stream));
    }

    public void save(String path) {
        this.save(new File(path));
    }

    public void save(File file) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        writer.print(this.yaml.dump(this.getData()));
        writer.close();
    }
}
