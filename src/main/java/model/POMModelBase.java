package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class POMModelBase {
    private CustomDistributionManagement distributionManagement;
    private Properties properties;
    private List<CustomDependency> dependencies = new LinkedList<>();
    private CustomReporting reporting;

    public CustomDistributionManagement getDistributionManagement() {
        return distributionManagement;
    }

    public Properties getProperties() {
        return properties;
    }

    public List<CustomDependency> getDependencies() {
        return dependencies;
    }

    public CustomReporting getReporting() {
        return reporting;
    }

    public void setDistributionManagement(CustomDistributionManagement distributionManagement) {
        this.distributionManagement = distributionManagement;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setDependencies(List<CustomDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public void setReporting(CustomReporting reporting) {
        this.reporting = reporting;
    }
}