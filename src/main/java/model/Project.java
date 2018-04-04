package model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.gradle.internal.impldep.org.apache.maven.model.*;

@JacksonXmlRootElement(localName = "employees")
public class Project {
    @JacksonXmlProperty(localName = "xmlns", isAttribute = true)
    private String xmlns;

    @JacksonXmlProperty(localName = "xmlns:xsi", isAttribute = true)
    private String xmlnsXsi;

    @JacksonXmlProperty(localName = "xsi:schemaLocation", isAttribute = true)
    private String xsiSchemaLocation;

    @JacksonXmlProperty(localName = "modelVersion")
    private String modelVersion;

    @JacksonXmlProperty(localName = "groupId")
    private String groupId;

    @JacksonXmlProperty(localName = "artifactId")
    private String artifcatId;

    @JacksonXmlProperty(localName = "packaging")
    private String packaging;

    @JacksonXmlProperty(localName = "version")
    private String version;

    @JacksonXmlProperty(localName = "distributionManagement")
    private DistributionManagement distributionManagement;

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "url")
    private String url;

    @JacksonXmlProperty(localName = "description")
    private String description;

    @JacksonXmlProperty(localName = "organization")
    private Organization organization;

    @JacksonXmlProperty(localName = "mailingLists")
    private MailingList[] mailingLists;

    @JacksonXmlProperty(localName = "licenses")
    private License[] licenses;

    @JacksonXmlProperty(localName = "scm")
    private Scm scm;

    @JacksonXmlProperty(localName = "developers")
    private Developer[] developers;

    public String getXmlns() {
        return xmlns;
    }

    public String getXmlnsXsi() {
        return xmlnsXsi;
    }

    public String getXsiSchemaLocation() {
        return xsiSchemaLocation;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifcatId() {
        return artifcatId;
    }

    public String getPackaging() {
        return packaging;
    }

    public String getVersion() {
        return version;
    }

    public DistributionManagement getDistributionManagement() {
        return distributionManagement;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public Organization getOrganization() {
        return organization;
    }

    public MailingList[] getMailingLists() {
        return mailingLists;
    }

    public License[] getLicenses() {
        return licenses;
    }

    public Scm getScm() {
        return scm;
    }

    public Developer[] getDevelopers() {
        return developers;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public void setXmlnsXsi(String xmlnsXsi) {
        this.xmlnsXsi = xmlnsXsi;
    }

    public void setXsiSchemaLocation(String xsiSchemaLocation) {
        this.xsiSchemaLocation = xsiSchemaLocation;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifcatId(String artifcatId) {
        this.artifcatId = artifcatId;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDistributionManagement(DistributionManagement distributionManagement) {
        this.distributionManagement = distributionManagement;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setMailingLists(MailingList[] mailingLists) {
        this.mailingLists = mailingLists;
    }

    public void setLicenses(License[] licenses) {
        this.licenses = licenses;
    }

    public void setScm(Scm scm) {
        this.scm = scm;
    }

    public void setDevelopers(Developer[] developers) {
        this.developers = developers;
    }

    public Project(String xmlns, String xmlnsXsi, String xsiSchemaLocation, String modelVersion, String groupId,
                   String artifcatId, String packaging, String version, DistributionManagement distributionManagement,
                   String name, String url, String description, Organization organization, MailingList[] mailingLists,
                   License[] licenses, Scm scm, Developer[] developers) {

        this.xmlns = xmlns;
        this.xmlnsXsi = xmlnsXsi;
        this.xsiSchemaLocation = xsiSchemaLocation;
        this.modelVersion = modelVersion;
        this.groupId = groupId;
        this.artifcatId = artifcatId;
        this.packaging = packaging;
        this.version = version;
        this.distributionManagement = distributionManagement;
        this.name = name;
        this.url = url;
        this.description = description;
        this.organization = organization;
        this.mailingLists = mailingLists;
        this.licenses = licenses;
        this.scm = scm;
        this.developers = developers;
    }
}