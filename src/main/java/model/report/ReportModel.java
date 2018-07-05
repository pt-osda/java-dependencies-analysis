package model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import model.Policy;
import java.io.Serializable;
import java.util.List;

public class ReportModel implements Serializable {
    private String id;
    private String version;
    private String name;
    private String description;
    private String timestamp;
    private String organization;

    @JsonProperty(value = "repo")
    private String repository;

    @JsonProperty(value = "repoOwner")
    private String repositoryOwner;

    private List<ReportDependencies> dependencies;

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

    public String getTimestamp() {
        return timestamp;
    }

    public String getOrganization() {
        return organization;
    }

    public String getRepository() {
        return repository;
    }

    public String getRepositoryOwner() {
        return repositoryOwner;
    }

    public List<ReportDependencies> getDependencies() {
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

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public void setRepositoryOwner(String repositoryOwner) {
        this.repositoryOwner = repositoryOwner;
    }

    public void setDependencies(List<ReportDependencies> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "ReportModel{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", organization='" + organization + '\'' +
                ", repository='" + repository + '\'' +
                ", repository owner='" + repositoryOwner + '\'' +
                ", dependencies=" + dependencies +
                '}';
    }

    public ReportModel(Policy policy){
        id = policy.getProjectId();
        version = "1.0.0";
        name = policy.getProjectName();
        description = String.format("All dependencies for the project %s, including their licenses and vulnerabilities", name);
        organization = policy.getOrganization() != null ? policy.getOrganization() : "No organization specified.";
        repository = policy.getRepository() != null ? policy.getOrganization() : "No repository specified.";
        repositoryOwner = policy.getRepositoryOwner() != null ? policy.getRepositoryOwner() : "No repository owner specified.";
    }
}