package model;

import java.util.List;

public class CustomReportSet extends CustomConfigurationContainer {
    private String id = "default";
    private List<String> reports;

    public String getId() {
        return id;
    }

    public List<String> getReports() {
        return reports;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReports(List<String> reports) {
        this.reports = reports;
    }
}