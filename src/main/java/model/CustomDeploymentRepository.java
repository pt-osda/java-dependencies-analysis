package model;

public class CustomDeploymentRepository {
    private boolean uniqueVersion;

    public boolean isUniqueVersion() {
        return uniqueVersion;
    }

    public void setUniqueVersion(boolean uniqueVersion) {
        this.uniqueVersion = uniqueVersion;
    }
}