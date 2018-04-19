package model;

import java.util.List;
import java.util.Properties;

public class CustomContributor {
    private String name;
    private String email;
    private String url;
    private String organization;
    private String organizationUrl;
    private List<String> roles;
    private String timezone;
    private Properties properties;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUrl() {
        return url;
    }

    public String getOrganization() {
        return organization;
    }

    public String getOrganizationUrl() {
        return organizationUrl;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getTimezone() {
        return timezone;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setOrganizationUrl(String organizationUrl) {
        this.organizationUrl = organizationUrl;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}