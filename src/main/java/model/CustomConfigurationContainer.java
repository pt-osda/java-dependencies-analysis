package model;public class CustomConfigurationContainer {
    private String inherited;
    private Object configuration;

    public String getInherited() {
        return inherited;
    }

    public Object getConfiguration() {
        return configuration;
    }

    public void setInherited(String inherited) {
        this.inherited = inherited;
    }

    public void setConfiguration(Object configuration) {
        this.configuration = configuration;
    }
}