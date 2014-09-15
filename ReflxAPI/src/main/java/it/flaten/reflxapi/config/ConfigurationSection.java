package it.flaten.reflxapi.config;

import java.util.Map;

public class ConfigurationSection {
    private Map<String, Object> data;

    protected ConfigurationSection() {

    }

    public ConfigurationSection(Map<String, Object> data) {
        this.data = data;
    }

    protected void setData(Map<String, Object> data) {
        this.data = data;
    }

    protected Map<String, Object> getData() {
        return this.data;
    }

    public ConfigurationSection getSection(String path) {
        Map<String, Object> toReturn = this.data;

        for (String key : path.split("."))
            toReturn = (Map<String, Object>) toReturn.get(key);

        return new ConfigurationSection(toReturn);
    }

    public Object getObject(String path) {
        int indexOf = path.indexOf(".");

        if (indexOf == -1)
            return this.data.get(path);

        int lastIndexOf = path.lastIndexOf(".");

        return this.getSection(path.substring(0, lastIndexOf)).getSection(path.substring(lastIndexOf + 1));
    }

    public String getString(String path) {
        return (String) this.getObject(path);
    }
}
