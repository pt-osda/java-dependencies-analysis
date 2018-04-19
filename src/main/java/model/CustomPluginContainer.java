package model;

import java.util.LinkedList;
import java.util.List;

public class CustomPluginContainer {
    private List<CustomPlugin> plugins = new LinkedList<>();

    public List<CustomPlugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<CustomPlugin> plugins) {
        this.plugins = plugins;
    }
}