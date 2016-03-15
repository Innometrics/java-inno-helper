package com.innometrics.integrationapp.model;


/**
 * Created by killpack on 17.12.15.
 */
public class ProfileStreamMessage {
    Profile profile;
    Meta meta;

    public ProfileStreamMessage(Profile profile) {
        this.profile = profile;
    }

    public ProfileStreamMessage() {
    }

    public Meta getMeta() {
        return meta;
    }

    public Profile getProfile() {
        return profile;
    }

}
