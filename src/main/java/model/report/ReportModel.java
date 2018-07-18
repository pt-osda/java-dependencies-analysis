package model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import model.Policy;
import java.io.Serializable;
import java.util.List;

public class ReportModel implements Serializable {
    // <editor-fold desc="Fields used to represent the report">
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

    private String admin;

    @JsonProperty(value = "error_info")
    private String errorInfo;

    private List<ReportDependencies> dependencies;

    private boolean successfulBuild;
    // </editor-fold>

    // <editor-fold desc="Getters of fields">
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

    public String getAdmin() {
        return admin;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public List<ReportDependencies> getDependencies() {
        return dependencies;
    }

    public boolean isSuccessfulBuild() {
        return successfulBuild;
    }
    // </editor-fold>

    // <editor-fold desc="Setters of fields">
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

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public void setDependencies(List<ReportDependencies> dependencies) {
        this.dependencies = dependencies;
    }

    public void setSuccessfulBuild(boolean successfulBuild) {
        this.successfulBuild = successfulBuild;
    }
    // </editor-fold>

    @Override
    public String toString() {
        return String.format("Id: %s, version: %s, name: %s, description: %s, timestamp: %s, organization: %s, repository: %s, repository owner: %s, dependencies: %s",
                id,
                version,
                name,
                description,
                timestamp,
                organization,
                repository,
                repositoryOwner,
                dependencies);
    }

    public ReportModel(Policy policy){
        id = policy.getProjectId();
        version = policy.getProjectVersion();
        name = policy.getProjectName();

        description = policy.getProjectDescription() != null ? policy.getProjectDescription() : String.format("All dependencies for the project %s, including their licenses and vulnerabilities", name);
        organization = policy.getOrganization();
        repository = policy.getRepository();
        repositoryOwner = policy.getRepositoryOwner();
        admin = policy.getAdministrator();
        errorInfo = "";
        successfulBuild = true;
    }
}