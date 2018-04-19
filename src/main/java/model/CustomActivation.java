package model;public class CustomActivation {
    private boolean activeByDefault = false;
    private String jdk;
    private CustomActivationOS os;
    private CustomActivationProperty property;
    private CustomActivationFile file;

    public boolean isActiveByDefault() {
        return activeByDefault;
    }

    public String getJdk() {
        return jdk;
    }

    public CustomActivationOS getOs() {
        return os;
    }

    public CustomActivationProperty getProperty() {
        return property;
    }

    public CustomActivationFile getFile() {
        return file;
    }

    public void setActiveByDefault(boolean activeByDefault) {
        this.activeByDefault = activeByDefault;
    }

    public void setJdk(String jdk) {
        this.jdk = jdk;
    }

    public void setOs(CustomActivationOS os) {
        this.os = os;
    }

    public void setProperty(CustomActivationProperty property) {
        this.property = property;
    }

    public void setFile(CustomActivationFile file) {
        this.file = file;
    }
}