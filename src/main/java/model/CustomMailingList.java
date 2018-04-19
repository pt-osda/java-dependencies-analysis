package model;import java.util.List;

public class CustomMailingList {
    private String name;
    private String subscribe;
    private String unsubscribe;
    private String post;
    private String archive;
    private List<String> otherArchives;

    public String getName() {
        return name;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public String getUnsubscribe() {
        return unsubscribe;
    }

    public String getPost() {
        return post;
    }

    public String getArchive() {
        return archive;
    }

    public List<String> getOtherArchives() {
        return otherArchives;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public void setUnsubscribe(String unsubscribe) {
        this.unsubscribe = unsubscribe;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public void setOtherArchives(List<String> otherArchives) {
        this.otherArchives = otherArchives;
    }
}