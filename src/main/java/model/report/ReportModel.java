package model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import model.Policy;
import java.io.Serializable;
import java.util.List;

public class ReportModel implements Serializable {
    // <editor-fold desc="Fields used to represent the report">
    /**
     * The id of the of the project the report gives information about.
     * <br>
     * Information obtained from the policy file.
     */
    private String id;

    /**
     * The version of the project the report gives information about.
     * <br>
     * Information obtained from the policy file.
     */
    private String version;

    /**
     * The name of the project the report gives information about.
     * <br>
     * Information obtained from the policy file.
     */
    private String name;

    /**
     * A description about the project the report gives information about.
     * <br>
     * Information might be obtained from the policy file if it is provided.
     */
    private String description;

    /**
     * The time when the report was produced.
     * <br>
     * Information obtained at the end of the plugins task.
     */
    private String timestamp;

    /**
     * The organization that the project belongs to.
     * <br>
     * Information might be obtained from the policy file if it is provided.
     */
    private String organization;

    /**
     * The report the project belongs to.
     * <br>
     * Information might be obtained from the policy file if it is provided.
     */
    @JsonProperty(value = "repo")
    private String repository;

    /**
     * The owner of the repository this project belongs to.
     * <br>
     * Information might be obtained from the policy file if it is provided.
     */
    @JsonProperty(value = "repoOwner")
    private String repositoryOwner;

    /**
     * The username of the administrator of this project.
     * <br>
     * Information obtained from the policy file.
     */
    @JsonProperty(value = "admin")
    private String administrator;

    /**
     * An error obtained during the execution of the plugin.
     * <br>
     * Might represent an error that happened while trying to obtain the vulnerabilities from an external Web API or that
     * the build failed, as indicated in the policy file.
     */
    @JsonProperty(value = "error_info")
    private String errorInfo;

    /**
     * The list of dependencies the project uses.
     */
    private List<ReportDependencies> dependencies;

    /**
     * Indicates if the plugins task finish successfully.
     */
    @JsonProperty(value = "successful_build")
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

    public String getAdministrator() {
        return administrator;
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

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
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
        administrator = policy.getAdministrator();
        errorInfo = "";
        successfulBuild = true;
    }
}