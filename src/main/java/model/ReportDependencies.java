package model;

import java.io.Serializable;
import java.util.Arrays;

public class ReportDependencies implements Serializable {
    /**
     * Indicates the title of the dependency to be presented.
     */
    private String title;

    /**
     * Indicates the version of the dependency in use.
     */
    private String main_version;

    /**
     * Indicates the license of the dependency if one was found.
     */
    private ReportLicense[] licenses;

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

    private int vulnerabilities_count;

    public String getTitle() {
        return title;
    }

    public String getMain_version() {
        return main_version;
    }

    public ReportLicense[] getLicenses() {
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

    public int getVulnerabilities_count() {
        return vulnerabilities_count;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMain_version(String main_version) {
        this.main_version = main_version;
    }

    public void setLicenses(ReportLicense[] licenses) {
        this.licenses = licenses;
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

    public void setVulnerabilities_count(int vulnerabilities_count) {
        this.vulnerabilities_count = vulnerabilities_count;
    }

    public ReportDependencies(String title, String main_version) {
        this.title = title;
        this.main_version = main_version;
        this.licenses = new ReportLicense[0];
        //this.parents = new ReportHierarchy[0];
        this.parents = new String[0];
        this.vulnerabilities = new ReportVulnerabilities[0];
    }

    @Override
    public String toString() {
        return String.format("Title: %s, main version: %s, license: %s, hierarchy: %s, vulnerabilities: %s",
                this.title,
                this.main_version,
                Arrays.toString(this.licenses),
                Arrays.toString(this.parents),
                Arrays.toString(this.vulnerabilities));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(ReportDependencies.class)){
            ReportDependencies dependency = (ReportDependencies) obj;
            return this.title.equals(dependency.title) && this.main_version.equals(dependency.main_version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.title.hashCode() * 2
                + this.main_version.hashCode() * 3;
    }
}
