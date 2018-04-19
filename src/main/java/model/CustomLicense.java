package model;

public class CustomLicense {
    private String name;
    private String url;
    private String distribution;
    private String comments;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDistribution() {
        return distribution;
    }

    public String getComments() {
        return comments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}