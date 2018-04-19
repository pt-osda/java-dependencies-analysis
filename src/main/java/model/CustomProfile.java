package model;

public class CustomProfile {
    private String id;
    private CustomActivation activation;
    private CustomBuildBase build;

    public String getId() {
        return id;
    }

    public CustomActivation getActivation() {
        return activation;
    }

    public CustomBuildBase getBuild() {
        return build;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setActivation(CustomActivation activation) {
        this.activation = activation;
    }

    public void setBuild(CustomBuildBase build) {
        this.build = build;
    }
}