package com.innometrics.integrationapp.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    private String id;

    private Map<String, Object> custom = new HashMap<>();

    private List<String> contacts;
    private Map<String, String> settings = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public Map<String, Object> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, Object> custom) {
        this.custom = custom;
    }
}
