package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Policy {
    //<editor-fold desc="fields included in a policy file">
    @JsonProperty(value = "project_id")
    private String projectId;

    @JsonProperty(value = "project_name")
    private String projectName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String organization;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "repo")
    private String repository;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "repo_owner")
    private String repositoryOwner;

    @JsonProperty(value = "invalid_licenses")
    private String[] invalidLicenses;

    private boolean fail;

    @JsonProperty(value = "api_cache_time")
    private int apiCacheTime;
    //</editor-fold>

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
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

    public String[] getInvalidLicenses() {
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

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public void setRepositoryOwner(String repositoryOwner) {
        this.repositoryOwner = repositoryOwner;
    }

    public void setInvalidLicenses(String[] invalidLicenses) {
        this.invalidLicenses = invalidLicenses;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

    public void setApiCacheTime(int apiCacheTime) {
        this.apiCacheTime = apiCacheTime;
    }
}