package com.innometrics.integrationapp.model;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public class App {

    private String id;

    private Map<String, JsonElement> custom = new HashMap<>();

    private Map<String, String> settings = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Map<String, JsonElement> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, JsonElement> custom) {
        this.custom = custom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        App app = (App) o;

        return !(id != null ? !id.equals(app.id) : app.id != null) &&
                !(custom != null ? !custom.equals(app.custom) : app.custom != null)
                && (settings != null ? settings.equals(app.settings) : app.settings == null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (custom != null ? custom.hashCode() : 0);
        result = 31 * result + (settings != null ? settings.hashCode() : 0);
        return result;
    }
}
