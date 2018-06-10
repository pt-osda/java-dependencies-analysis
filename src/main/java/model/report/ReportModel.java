package model.report;

import java.io.Serializable;
import java.util.List;

public class ReportModel implements Serializable {
    private String id;
    private String version;
    private String name;
    private String description;
    private List<ReportDependencies> dependencies;
    private String timestamp;

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

    public List<ReportDependencies> getDependencies() {
        return dependencies;
    }

    public String getTimestamp() {
        return timestamp;
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

    public void setDependencies(List<ReportDependencies> dependencies) {
        this.dependencies = dependencies;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ReportModel{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dependencies=" + dependencies +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    public ReportModel(String projectName){
        id = "Id";
        version = "1.0.0";
        name = String.format("Report for project %s", projectName);
        description = String.format("All dependencies for the project %s, including their licenses and vulnerabilities", projectName);
    }
}