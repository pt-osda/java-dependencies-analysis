package model;import java.util.List;

public class CustomDependency {
    private String groupId;
    private String artifactId;
    private String version;
    private String typ = "jar";
    private String classifier;
    private String scope;
    private String systemPath;
    private List<CustomExclusion> exclusions;
    private String optional;

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getTyp() {
        return typ;
    }

    public String getClassifier() {
        return classifier;
    }

    public String getScope() {
        return scope;
    }

    public String getSystemPath() {
        return systemPath;
    }

    public List<CustomExclusion> getExclusions() {
        return exclusions;
    }

    public String getOptional() {
        return optional;
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

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setSystemPath(String systemPath) {
        this.systemPath = systemPath;
    }

    public void setExclusions(List<CustomExclusion> exclusions) {
        this.exclusions = exclusions;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }
}