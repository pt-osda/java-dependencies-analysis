package model;

import java.util.LinkedList;
import java.util.List;

public class CustomPatternSet {
    private List<String> includes = new LinkedList<>();
    private List<String> excludes = new LinkedList<>();

    public List<String> getIncludes() {
        return includes;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }
}