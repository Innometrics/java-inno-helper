package com.innometrics.integrationapp.model;

import com.google.gson.JsonElement;

/**
 * Created by killpack on 17.12.15.
 */
public class PofileStreamMessage {
    Profile profile;
    Object meta;

    public Object getMeta() {
        return meta;
    }

    public Profile getProfile() {
        return profile;
    }

}
