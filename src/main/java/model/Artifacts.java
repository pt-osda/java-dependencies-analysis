package model;

import java.io.Serializable;

public class Artifacts implements Serializable {
    //<editor-fold desc="Fields of the objects send to API">
    private String pm;
    private String name;
    private String version;
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

    public Artifacts(String name, String version, String group) {
        pm = "maven";
        this.name = name;
        this.version = version;
        this.group = group;
    }

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
}