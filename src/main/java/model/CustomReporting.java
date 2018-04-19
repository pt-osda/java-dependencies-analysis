package model;

import java.util.List;

public class CustomReporting {
    private String excludeDefaults;
    private String outputDirectory;
    private List<CustomReportPlugin> plugins;

    public String getExcludeDefaults() {
        return excludeDefaults;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public List<CustomReportPlugin> getPlugins() {
        return plugins;
    }

    public void setExcludeDefaults(String excludeDefaults) {
        this.excludeDefaults = excludeDefaults;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setPlugins(List<CustomReportPlugin> plugins) {
        this.plugins = plugins;
    }
}