package model;

import java.util.*;

public class POMModel extends POMModelBase{
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
    private List<CustomDeveloper> developers = new LinkedList<>();
    private List<CustomContributor> contributors = new LinkedList<>();
    private List<CustomMailingList> mailingLists = new LinkedList<>();
    private CustomPrerequisites prerequisites;
    private CustomScm scm;
    private CustomIssueManagement issueManagement;
    private CustomCiManagement ciManagement;
    private CustomBuild build;
    private List<CustomProfile> profiles = new LinkedList<>();
    private String modelEncoding;

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

    public List<CustomDeveloper> getDevelopers() {
        return developers;
    }

    public List<CustomContributor> getContributors() {
        return contributors;
    }

    public List<CustomMailingList> getMailingLists() {
        return mailingLists;
    }

    public CustomPrerequisites getPrerequisites() {
        return prerequisites;
    }

    public CustomScm getScm() {
        return scm;
    }

    public CustomIssueManagement getIssueManagement() {
        return issueManagement;
    }

    public CustomCiManagement getCiManagement() {
        return ciManagement;
    }

    public CustomBuild getBuild() {
        return build;
    }

    public List<CustomProfile> getProfiles() {
        return profiles;
    }

    public String getModelEncoding() {
        return modelEncoding;
    }

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

    public void setDevelopers(List<CustomDeveloper> developers) {
        this.developers = developers;
    }

    public void setContributors(List<CustomContributor> contributors) {
        this.contributors = contributors;
    }

    public void setMailingLists(List<CustomMailingList> mailingLists) {
        this.mailingLists = mailingLists;
    }

    public void setPrerequisites(CustomPrerequisites prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void setScm(CustomScm scm) {
        this.scm = scm;
    }

    public void setIssueManagement(CustomIssueManagement issueManagement) {
        this.issueManagement = issueManagement;
    }

    public void setCiManagement(CustomCiManagement ciManagement) {
        this.ciManagement = ciManagement;
    }

    public void setBuild(CustomBuild build) {
        this.build = build;
    }

    public void setProfiles(List<CustomProfile> profiles) {
        this.profiles = profiles;
    }

    public void setModelEncoding(String modelEncoding) {
        this.modelEncoding = modelEncoding;
    }
}