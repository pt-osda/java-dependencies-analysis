package model;

import java.io.Serializable;

public class ReportVulnerabilities implements Serializable {
    /**
     * Indicates the name of the vulnerability found.
     */
    private String title;

    /**
     * Describes the vulnerability and what it can do.
     */
    private String description;
    private String[] references;    // TODO find what this field means.

    /**
     * Indicates all the versions of the current dependency vulnerable to the vulnerability.
     */
    private String[] versions;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String[] getReferences() {
        return references;
    }

    public String[] getVersions() {
        return versions;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReferences(String[] references) {
        this.references = references;
    }

    public void setVersions(String[] versions) {
        this.versions = versions;
    }

    public ReportVulnerabilities(String title, String description, String[] references, String[] versions) {
        this.title = title;
        this.description = description;
        this.references = references;
        this.versions = versions;
    }
}