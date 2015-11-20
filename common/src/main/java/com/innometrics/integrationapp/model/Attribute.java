package com.innometrics.integrationapp.model;


import java.util.Map;

public class Attribute {

    private String collectApp;
    private String section;
    private Map<String, Object> data;

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
        this.collectApp = collectApp;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
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

//    public int compareTo(Attribute o) {
//        if (o == null) throw new NullPointerException();
//        return hashCode() - o.hashCode();
//    }
}
