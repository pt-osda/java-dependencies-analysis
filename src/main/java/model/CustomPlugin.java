package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomPlugin extends CustomConfigurationContainer{
    private String groupId;
    private String artifactId;
    private String version;
    private String extensions;
    private List<CustomPluginExecution> executions = new LinkedList<>();
    private List<CustomDependency> dependencies = new LinkedList<>();
    private Object goals;
    private Map<String, CustomPluginExecution> executionMap = new HashMap<>();
    private String key;

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getExtensions() {
        return extensions;
    }

    public List<CustomPluginExecution> getExecutions() {
        return executions;
    }

    public List<CustomDependency> getDependencies() {
        return dependencies;
    }

    public Object getGoals() {
        return goals;
    }

    public Map<String, CustomPluginExecution> getExecutionMap() {
        return executionMap;
    }

    public String getKey() {
        return key;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public void setExecutions(List<CustomPluginExecution> executions) {
        this.executions = executions;
    }

    public void setDependencies(List<CustomDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public void setGoals(Object goals) {
        this.goals = goals;
    }

    public void setExecutionMap(Map<String, CustomPluginExecution> executionMap) {
        this.executionMap = executionMap;
    }

    public void setKey(String key) {
        this.key = key;
    }
}