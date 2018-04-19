package model;public class CustomPluginConfiguration extends CustomPluginContainer {
    private CustomPluginManagement pluginManagement;

    public CustomPluginManagement getPluginManagement() {
        return pluginManagement;
    }

    public void setPluginManagement(CustomPluginManagement pluginManagement) {
        this.pluginManagement = pluginManagement;
    }
}