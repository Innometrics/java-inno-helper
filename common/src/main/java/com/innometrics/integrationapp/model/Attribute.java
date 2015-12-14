package com.innometrics.integrationapp.model;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Attribute extends Dirty {

    private String collectApp;
    private String section;
    private Map<String, Object> data = new HashMap<>();

    public Attribute() {
    }

    public Attribute(String collectApp, String section) {
        this.collectApp = collectApp;
        this.section = section;
    }

    public String getCollectApp() {
        return collectApp;

    }

    public void setCollectApp(String collectApp) {
        setDirty(true);
        this.collectApp = collectApp;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        setDirty(true);
        this.section = section;
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    public void setData(Map<String, Object> data) {
        setDirty(true);
        this.data = data;
    }

    public void putData(String key, Object value) {
        setDirty(true);
        data.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribute)) return false;
        Attribute attribute = (Attribute) o;
        return collectApp.equals(attribute.collectApp) &&
                data.equals(attribute.data) &&
                section.equals(attribute.section);
    }

    @Override
    public int hashCode() {
        int result = collectApp.hashCode();
        result = 31 * result + section.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }

    public void resetDirty() {
        setDirty(false);
    }
}
