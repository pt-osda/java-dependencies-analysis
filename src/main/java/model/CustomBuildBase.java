package model;

import java.util.LinkedList;
import java.util.List;

public class CustomBuildBase extends CustomPluginConfiguration{
    private String defaultGoal;
    private List<CustomResource> resources = new LinkedList<>();
    private List<CustomResource> testResources = new LinkedList<>();
    private String directory;
    private String finalName;
    private List<String> filters = new LinkedList<>();

    public String getDefaultGoal() {
        return defaultGoal;
    }

    public List<CustomResource> getResources() {
        return resources;
    }

    public List<CustomResource> getTestResources() {
        return testResources;
    }

    public String getDirectory() {
        return directory;
    }

    public String getFinalName() {
        return finalName;
    }

    public List<String> getFilters() {
        return filters;
    }

    public void setDefaultGoal(String defaultGoal) {
        this.defaultGoal = defaultGoal;
    }

    public void setResources(List<CustomResource> resources) {
        this.resources = resources;
    }

    public void setTestResources(List<CustomResource> testResources) {
        this.testResources = testResources;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setFinalName(String finalName) {
        this.finalName = finalName;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }
}