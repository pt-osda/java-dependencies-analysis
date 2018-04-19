package model;

public class CustomRelocation {
    private String groupId;
    private String artifactId;
    private String version;
    private String message;

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getMessage() {
        return message;
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

    public void setMessage(String message) {
        this.message = message;
    }
}