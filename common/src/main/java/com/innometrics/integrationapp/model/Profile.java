package com.innometrics.integrationapp.model;

import java.util.*;


public class Profile {

    private String id;
    private String version = "1.0";
    private Date createdAt;

    private List<Session> sessions = new ArrayList<Session>();
    private List<Attribute> attributes = new ArrayList<Attribute>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        if(sessions == null) {
            sessions = new ArrayList<Session>();
        }

        this.sessions = sessions;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        if(attributes == null) {
            attributes = new ArrayList<Attribute>();
        }
        this.attributes = attributes;
    }
    public void setAttributes( String collectApp,String section , String name , Object value) {

        if(attributes == null) {
            attributes = new ArrayList<Attribute>();
        }
        Attribute attribute= new Attribute(collectApp,section);
        attribute.getData().put(name,value);
        this.attributes.add(attribute);
    }

}
