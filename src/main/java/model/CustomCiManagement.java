package model;

import java.util.LinkedList;
import java.util.List;

public class CustomCiManagement {
    private String system;
    private String url;
    private List<CustomNotifier> notifiers = new LinkedList<>();

    public String getSystem() {
        return system;
    }

    public String getUrl() {
        return url;
    }

    public List<CustomNotifier> getNotifiers() {
        return notifiers;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNotifiers(List<CustomNotifier> notifiers) {
        this.notifiers = notifiers;
    }
}