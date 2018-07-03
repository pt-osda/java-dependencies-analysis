package model.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<String> children;

    /**
     * Indicates all the vulnerabilities found in this dependency.
     */
    private ReportVulnerabilities[] vulnerabilities;

    /**
     * Indicates the number of vulnerabilities present in this dependency
     */
    @JsonProperty(value = "vulnerabilities_count")
    private int vulnerabilitiesCount;

    /**
     * Indicates whether this dependency is being used directly by the project or if it's a transitive dependency
     */
    private boolean direct;
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

    public List<String> getChildren() {
        return children;
    }

    public ReportVulnerabilities[] getVulnerabilities() {
        return vulnerabilities;
    }

    public int getVulnerabilitiesCount() {
        return vulnerabilitiesCount;
    }

    public boolean isDirect() {
        return direct;
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

    public void addChildren(String children) {
        this.children.add(children);
    }

    public void setVulnerabilities(ReportVulnerabilities[] vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    public void setVulnerabilitiesCount(int vulnerabilitiesCount) {
        this.vulnerabilitiesCount = vulnerabilitiesCount;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }
    // </editor-fold>

    // <editor-fold desc="Override methods">
    @Override
    public String toString() {
        return String.format("Title: %s, main version: %s, license: %s, hierarchy: %s, vulnerabilities: %s",
                this.title,
                this.mainVersion,
                this.licenses.toString(),
                this.children,
                Arrays.toString(this.vulnerabilities));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(ReportDependencies.class)){
            ReportDependencies dependency = (ReportDependencies) obj;
            return this.title.equals(dependency.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.title.hashCode() * 2;
    }
    // </editor-fold>

    public ReportDependencies(String title, String mainVersion, boolean direct) {
        this.title = title;
        this.mainVersion = mainVersion;
        this.licenses = new ArrayList<>();
        this.children = new ArrayList<>();
        this.vulnerabilities = new ReportVulnerabilities[0];
        this.direct = direct;
    }

    @JsonIgnore
    public String getGroup() {
        return this.title.split(":")[0];
    }

    @JsonIgnore
    public String getArtifactId() {
        return this.title.split(":")[1];
    }
}