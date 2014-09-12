package it.flaten.reflx.plugin;

import it.flaten.reflxapi.plugin.PluginDescriptor;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class ReflxPluginDescriptor implements PluginDescriptor {
    private final Map<String, String> data;

    public ReflxPluginDescriptor(ReflxPluginContainer container) {
        this.data = (Map<String, String>) new Yaml().load(container.getResourceAsStream("plugin.yml"));
    }

    @Override
    public String getName() {
        return this.data.get("name");
    }

    @Override
    public String getVersion() {
        return this.data.get("version");
    }

    @Override
    public String getMain() {
        return this.data.get("main");
    }
}
