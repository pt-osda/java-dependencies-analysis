package model.pomRepresentation;

public class CustomParent {
    //<editor-fold desc="Fields of xml representation">
    private String groupId;
    private String artifactId;
    private String version;
    private String relativePath;
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">
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
    //</editor-fold>
}