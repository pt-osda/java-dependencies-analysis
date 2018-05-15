package model;

import java.io.Serializable;

public class ReportModel implements Serializable {
    private String id;
    private String version;
    private String name;
    private String description;
    private ReportDependencies[] dependencies;

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ReportDependencies[] getDependencies() {
        return dependencies;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDependencies(ReportDependencies[] dependencies) {
        this.dependencies = dependencies;
    }

    public ReportModel(String projectName){
        id = "Id";
        version = "1.0.0";
        name = String.format("Report for project %s", projectName);
        description = String.format("All dependencies for the project %s, including their licenses and vulnerabilities", projectName);
    }
}