package model;

import java.util.List;

public class VulnerabilitiesResult {
    private String title;
    private String mainVersion;
    private int totalVulnerabilities;
    private List<ReportVulnerabilities> vulnerabilities;

    public String getTitle() {
        return title;
    }

    public String getMainVersion() {
        return mainVersion;
    }

    public int getTotalVulnerabilities() {
        return totalVulnerabilities;
    }

    public List<ReportVulnerabilities> getVulnerabilities() {
        return vulnerabilities;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMainVersion(String mainVersion) {
        this.mainVersion = mainVersion;
    }

    public void setTotalVulnerabilities(int totalVulnerabilities) {
        this.totalVulnerabilities = totalVulnerabilities;
    }

    public void setVulnerabilities(List<ReportVulnerabilities> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    @Override
    public String toString() {
        return String.format("Title: %s, main version: %s, total vulnerabilities: %s, vulnerabilities: %s", title, mainVersion, totalVulnerabilities, vulnerabilities);
    }
}
