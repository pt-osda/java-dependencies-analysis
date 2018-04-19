package model;

import java.util.List;

public class CustomPluginExecution extends CustomConfigurationContainer{
    private String id;
    private String phase;
    private int priority;
    private List<String> goals;

    public String getId() {
        return id;
    }

    public String getPhase() {
        return phase;
    }

    public int getPriority() {
        return priority;
    }

    public List<String> getGoals() {
        return goals;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }
}