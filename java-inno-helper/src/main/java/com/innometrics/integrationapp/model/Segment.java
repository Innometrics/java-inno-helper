package com.innometrics.integrationapp.model;

/**
 * @author Qambber Hussain, Innomerics
 */
public class Segment {
    private String id;
    private String name;
    private boolean enabled;
    private String iql;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getIql() {
        return iql;
    }

    public void setIql(String iql) {
        this.iql = iql;
    }
}


