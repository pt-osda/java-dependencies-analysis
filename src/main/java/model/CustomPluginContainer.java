package model;import java.util.List;

public class CustomPluginContainer {
    private List<CustomPlugin> plugins;

    public List<CustomPlugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<CustomPlugin> plugins) {
        this.plugins = plugins;
    }
}