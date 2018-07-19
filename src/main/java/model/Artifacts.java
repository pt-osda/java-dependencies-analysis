package model;

import java.io.Serializable;

public class Artifacts implements Serializable {
    //<editor-fold desc="Fields of the objects send to API">
    /**
     * The project manager of the current dependency represented as an artifact.
     * <br>
     * Since this plugin is meant to run in a Gradle build process its value is always "maven", which is the package
     * manager of Gradle.
     */
    private String pm;

    /**
     * The name, artifact Id, of the current dependency represented as an artifact
     */
    private String name;

    /**
     * The version of the current dependency represented as an artifact
     */
    private String version;

    /**
     * The group Id of the current dependency represented as an artifact
     */
    private String group;
    //</editor-fold>

    public String getPm() {
        return pm;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getGroup() {
        return group;
    }

    public void setPm(String packageManager) {
        this.pm = packageManager;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    //<editor-fold desc="Override methods">
    @Override
    public String toString() {
        return String.format("\"pm\" \"%s\", \"name\" \"%s\", \"version\" \"%s\", \"group\" \"%s\"", pm, name, version, group);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == Artifacts.class) {
            Artifacts artifacts = (Artifacts) obj;
            return this.pm.equals(artifacts.pm)
                    && this.group.equals(artifacts.group)
                    && this.name.equals(artifacts.name)
                    && this.version.equals(artifacts.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.pm.hashCode() * 2
                + this.group.hashCode() * 3
                + this.name.hashCode() * 5
                + this.version.hashCode() * 7;
    }
    //</editor-fold>

    public Artifacts(String name, String version, String group) {
        pm = "maven";
        this.name = name;
        this.version = version;
        this.group = group;
    }
}