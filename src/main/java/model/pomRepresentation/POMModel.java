package model.pomRepresentation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.*;

public class POMModel {
    //<editor-fold desc="Fields of xml representation">
    private String schemaLocation;  // não está presente no model de maven
    private String modelVersion;
    private CustomParent parent;
    private String groupId;
    private String artifactId;
    private String version;
    private String packaging;
    private String name;
    private String description;
    private String url;
    private String inceptionYear;
    private CustomOrganization organization;
    private List<CustomLicense> licenses = new LinkedList<>();
    private List<CustomDependency> dependencies = new LinkedList<>();
    private CustomScm scm;
    private CustomIssueManagement issueManagement;

    @JsonIgnore
    private List<Object> developers = new LinkedList<>();

    @JsonIgnore
    private List<Object> contributors = new LinkedList<>();

    @JsonIgnore
    private List<Object> mailingLists = new LinkedList<>();

    @JsonIgnore
    private Object prerequisites;

    @JsonIgnore
    private Object ciManagement;

    @JsonIgnore
    private Object build;

    @JsonIgnore
    private List<Object> profiles = new LinkedList<>();

    @JsonIgnore
    private String modelEncoding;

    @JsonIgnore
    private Object distributionManagement;

    @JsonIgnore
    private Object properties;

    @JsonIgnore
    private Object reporting;
    //</editor-fold>

    //<editor-fold desc="Getters">
    public String getSchemaLocation() {
        return schemaLocation;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public CustomParent getParent() {
        return parent;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getPackaging() {
        return packaging;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getInceptionYear() {
        return inceptionYear;
    }

    public CustomOrganization getOrganization() {
        return organization;
    }

    public List<CustomLicense> getLicenses() {
        return licenses;
    }

    public List<CustomDependency> getDependencies() {
        return dependencies;
    }

    public CustomScm getScm() {
        return scm;
    }

    public CustomIssueManagement getIssueManagement() {
        return issueManagement;
    }

    public List<Object> getDevelopers() {
        return developers;
    }

    public List<Object> getContributors() {
        return contributors;
    }

    public List<Object> getMailingLists() {
        return mailingLists;
    }

    public Object getPrerequisites() {
        return prerequisites;
    }

    public Object getCiManagement() {
        return ciManagement;
    }

    public Object getBuild() {
        return build;
    }

    public List<Object> getProfiles() {
        return profiles;
    }

    public String getModelEncoding() {
        return modelEncoding;
    }

    public Object getDistributionManagement() {
        return distributionManagement;
    }

    public Object getProperties() {
        return properties;
    }

    public Object getReporting() {
        return reporting;
    }
    //</editor-fold>

    //<editor-fold desc="Setters">
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public void setParent(CustomParent parent) {
        this.parent = parent;
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

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setInceptionYear(String inceptionYear) {
        this.inceptionYear = inceptionYear;
    }

    public void setOrganization(CustomOrganization organization) {
        this.organization = organization;
    }

    public void setLicenses(List<CustomLicense> licenses) {
        this.licenses = licenses;
    }

    public void setDependencies(List<CustomDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public void setScm(CustomScm scm) {
        this.scm = scm;
    }

    public void setIssueManagement(CustomIssueManagement issueManagement) {
        this.issueManagement = issueManagement;
    }

    public void setDevelopers(List<Object> developers) {
        this.developers = developers;
    }

    public void setContributors(List<Object> contributors) {
        this.contributors = contributors;
    }

    public void setMailingLists(List<Object> mailingLists) {
        this.mailingLists = mailingLists;
    }

    public void setPrerequisites(Object prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void setCiManagement(Object ciManagement) {
        this.ciManagement = ciManagement;
    }

    public void setBuild(Object build) {
        this.build = build;
    }

    public void setProfiles(List<Object> profiles) {
        this.profiles = profiles;
    }

    public void setModelEncoding(String modelEncoding) {
        this.modelEncoding = modelEncoding;
    }

    public void setDistributionManagement(Object distributionManagement) {
        this.distributionManagement = distributionManagement;
    }

    public void setProperties(Object properties) {
        this.properties = properties;
    }

    public void setReporting(Object reporting) {
        this.reporting = reporting;
    }
    //</editor-fold>
}