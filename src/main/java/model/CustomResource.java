package model;

public class CustomResource extends CustomFileSet {
    private String targetPath;
    private String filtering;
    private String mergeId;

    public String getTargetPath() {
        return targetPath;
    }

    public String getFiltering() {
        return filtering;
    }

    public String getMergeId() {
        return mergeId;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public void setFiltering(String filtering) {
        this.filtering = filtering;
    }

    public void setMergeId(String mergeId) {
        this.mergeId = mergeId;
    }
}