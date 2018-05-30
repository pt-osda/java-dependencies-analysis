package model.pomRepresentation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedList;
import java.util.List;

public class CustomDependency {
    //<editor-fold desc="Fields of xml representation">
    private String groupId;
    private String artifactId;
    private String version;
    private String typ = "jar";
    private String classifier;
    private String scope;
    private String systemPath;
    private String optional;

    @JsonIgnore
    private List<Object> exclusions = new LinkedList<>();
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

    public String getOptional() {
        return optional;
    }

    public List<Object> getExclusions() {
        return exclusions;
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

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public void setExclusions(List<Object> exclusions) {
        this.exclusions = exclusions;
    }
    //</editor-fold>
}