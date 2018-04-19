package model;

public class CustomScm {
    private String connection;
    private String developerConnection;
    private String tag;
    private String url;

    public String getConnection() {
        return connection;
    }

    public String getDeveloperConnection() {
        return developerConnection;
    }

    public String getTag() {
        return tag;
    }

    public String getUrl() {
        return url;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public void setDeveloperConnection(String developerConnection) {
        this.developerConnection = developerConnection;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}