package model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.gradle.api.logging.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportDependencies implements Serializable {
    // <editor-fold desc="Fields used to describe a dependency in the report">
    /**
     * Indicates the title of the dependency to be presented.
     */
    private String title;

    /**
     * Indicates the version of the dependency in use.
     */
    @JsonProperty(value = "main_version")
    private String mainVersion;

    /**
     * Indicates the license of the dependency if one was found.
     */
    private List<ReportLicense> licenses;

    /**
     * Indicates the hierarchy of the dependency. Showing if the dependency is used directly by the project or if it's a
     * dependency of a dependency and if so shows how "far" it is from the direct dependency
     */
    //private ReportHierarchy[] parents;  // TODO find the information needed for this field.
    private String[] parents;

    /**
     * Indicates all the vulnerabilities found in this dependency.
     */
    private ReportVulnerabilities[] vulnerabilities;

    @JsonProperty(value = "vulnerabilities_count")
    private int vulnerabilitiesCount;
    // </editor-fold>

    // <editor-fold desc="Getters of fields">
    public String getTitle() {
        return title;
    }

    public String getMainVersion() {
        return mainVersion;
    }

    public List<ReportLicense> getLicenses() {
        return licenses;
    }

    /*public ReportHierarchy[] getParents() {
        return parents;
    }*/

    public String[] getParents() {
        return parents;
    }

    public ReportVulnerabilities[] getVulnerabilities() {
        return vulnerabilities;
    }

    public int getVulnerabilitiesCount() {
        return vulnerabilitiesCount;
    }
    // </editor-fold>

    // <editor-fold desc="Setter of fields">
    public void setTitle(String title) {
        this.title = title;
    }

    public void setMainVersion(String mainVersion) {
        this.mainVersion = mainVersion;
    }

    public void addLicense(ReportLicense license) {
        this.licenses.add(license);
    }

    /*public void setParents(ReportHierarchy[] parents) {
        this.parents = parents;
    }*/

    public void setParents(String[] parents) {
        this.parents = parents;
    }

    public void setVulnerabilities(ReportVulnerabilities[] vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    public void setVulnerabilitiesCount(int vulnerabilitiesCount) {
        this.vulnerabilitiesCount = vulnerabilitiesCount;
    }
    // </editor-fold>

    // <editor-fold desc="Override methods">
    @Override
    public String toString() {
        return String.format("Title: %s, main version: %s, license: %s, hierarchy: %s, vulnerabilities: %s",
                this.title,
                this.mainVersion,
                this.licenses.toString(),
                Arrays.toString(this.parents),
                Arrays.toString(this.vulnerabilities));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(ReportDependencies.class)){
            ReportDependencies dependency = (ReportDependencies) obj;
            return this.title.equals(dependency.title) && this.mainVersion.equals(dependency.mainVersion);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.title.hashCode() * 2
                + this.mainVersion.hashCode() * 3;
    }
    // </editor-fold>

    public ReportDependencies(String title, String mainVersion) {
        this.title = title;
        this.mainVersion = mainVersion;
        this.licenses = new ArrayList<>();
        //this.parents = new ReportHierarchy[0];
        this.parents = new String[0];
        this.vulnerabilities = new ReportVulnerabilities[0];
    }
}
