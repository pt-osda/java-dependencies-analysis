package model.pomRepresentation;

public class CustomLicense {
    //<editor-fold desc="Fields of xml representation">
    private String name;
    private String url;
    private String distribution;
    private String comments;
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">
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
    //</editor-fold>

    @Override
    public String toString() {
        return String.format("name: %s, url: %s, distribution: %s, comments: %s", name, url, distribution, comments);
    }
}