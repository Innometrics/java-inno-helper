package com.innometrics.integrationapp.model;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    private String id;

    private Map<String, JsonElement> custom = new HashMap<>();

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

    public Map<String, JsonElement> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, JsonElement> custom) {
        this.custom = custom;
    }
}
