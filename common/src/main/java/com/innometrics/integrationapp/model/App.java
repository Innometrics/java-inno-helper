package com.innometrics.integrationapp.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    private String id;


    private Map<String, Object> custom = new HashMap<>();

    private List<String> contacts;
    private Map<String, String> settings = new HashMap<>();

    /**
     * This method will be removed in the near future. Please use custom instead.
     * @return
     */

    /**
     * This method will be removed in the near future. Please use custom instead.
     * @param rules
     */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method will be removed in the near future. Please use custom instead.
     * @param settings
     */

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
