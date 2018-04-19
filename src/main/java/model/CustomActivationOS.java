package model;public class CustomActivationOS {
    private String name;
    private String family;
    private String arch;
    private String version;

    public String getName() {
        return name;
    }

    public String getFamily() {
        return family;
    }

    public String getArch() {
        return arch;
    }

    public String getVersion() {
        return version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}