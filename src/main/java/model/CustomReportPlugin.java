package model;

import java.util.LinkedList;
import java.util.List;

public class CustomReportPlugin extends CustomConfigurationContainer{
    private String groupId;
    private String artifactId;
    private String version;
    private List<CustomReportSet> reportSets = new LinkedList<>();

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public List<CustomReportSet> getReportSets() {
        return reportSets;
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

    public void setReportSets(List<CustomReportSet> reportSets) {
        this.reportSets = reportSets;
    }
}