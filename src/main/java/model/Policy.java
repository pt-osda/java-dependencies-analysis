package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Policy {
    //<editor-fold desc="fields included in a policy file">
    @JsonProperty(value = "project_id")
    private String projectId;

    @JsonProperty(value = "project_name")
    private String projectName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "project_version")
    private String projectVersion;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "project_description")
    private String projectDescription;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String organization;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "repo")
    private String repository;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "repo_owner")
    private String repositoryOwner;

    @JsonProperty(value = "admin")
    private String administrator;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "invalid_licenses")
    private List<String> invalidLicenses = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean fail = false;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "api_cache_time")
    private int apiCacheTime = 0;
    //</editor-fold>

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public String getProjectDescription() {
        return projectDescription;
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

    public List<String> getInvalidLicenses() {
        return invalidLicenses;
    }

    public boolean isFail() {
        return fail;
    }

    public int getApiCacheTime() {
        return apiCacheTime;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
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

    public void setInvalidLicenses(List<String> invalidLicenses) {
        this.invalidLicenses = invalidLicenses;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

    public void setApiCacheTime(int apiCacheTime) {
        this.apiCacheTime = apiCacheTime;
    }

    @Override
    public String toString() {
        return String.format("project id: %s, project name: %s, project version: %s, project description: %s, " +
                        "organization: %s, repository: %s, repository owner: %s, administrator: %s, invalid licenses: " +
                        "%s, fail: %s, api cache time: %s",
                projectId,
                projectName,
                projectVersion,
                projectDescription,
                organization,
                repository,
                repositoryOwner,
                administrator,
                invalidLicenses,
                fail,
                apiCacheTime);
    }
}