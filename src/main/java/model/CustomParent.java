package model;

public class CustomParent {
    private String groupId;
    private String artifactId;
    private String version;
    private String relativePath;

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getRelativePath() {
        return relativePath;
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

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
}