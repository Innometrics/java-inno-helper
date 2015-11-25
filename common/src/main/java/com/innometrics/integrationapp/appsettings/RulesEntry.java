package com.innometrics.integrationapp.appsettings;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RulesEntry {

    private String id;
    private Map<String,Object> ruleSettings;
    private List<FieldSetsEntry> fieldSets = new ArrayList<>();
    private String event;
    private String name;
    private List<String> segmentsLinked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getSegmentsLinked() {
        return segmentsLinked;
    }

    public void setSegmentsLinked(List<String> segmentsLinked) {
        this.segmentsLinked = segmentsLinked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<FieldSetsEntry> getFieldSets() {
        return fieldSets;
    }

    public void setFieldSets(List<FieldSetsEntry> fieldSets) {
        this.fieldSets = fieldSets;
    }

    public Map<String, Object> getRuleSettings() {
        return ruleSettings;
    }

    public void setRuleSettings(Map<String, Object> ruleSettings) {
        this.ruleSettings = ruleSettings;
    }
}
