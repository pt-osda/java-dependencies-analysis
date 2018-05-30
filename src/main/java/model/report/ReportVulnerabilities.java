package model.report;

import java.io.Serializable;
import java.util.Arrays;

public class ReportVulnerabilities implements Serializable {
    // <editor-fold dec="Fields used to represent a vulnerability in the report.">
    /**
     * Indicates the name of the vulnerability found.
     */
    private String title;

    /**
     * Unique identifier of the vulnerability in the dependency it was found on.
     */
    private long id;

    /**
     * Describes the vulnerability and what it can do.
     */
    private String description;
    private String[] references;    // TODO find what this field means.

    /**
     * Indicates all the versions of the current dependency vulnerable to the vulnerability.
     */
    private String[] versions;
    // </editor-fold>

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
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

    public void setId(long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return String.format("Title: %s, id: %s, description: %s, references: %s, version: %s", title, id, description, Arrays.toString(references), Arrays.toString(versions));
    }

    public ReportVulnerabilities(String title, String description, String[] references, String[] versions) {
        this.title = title;
        this.description = description;
        this.references = references;
        this.versions = versions;
    }
}