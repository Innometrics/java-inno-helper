package com.innometrics.integrationapp.model;

import java.util.*;


public class Profile extends Dirty {

    private String id;
    private String version = "1.0";
    private Date createdAt;

    private List<Session> sessions = new ArrayList<Session>();
    private List<Attribute> attributes = new ArrayList<Attribute>();
    private Set<String> mergedProfiles = new LinkedHashSet();
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
        return Collections.unmodifiableList(sessions);
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
        return Collections.unmodifiableList(attributes);
    }

    public Set<String> getMergedProfiles() {
        return mergedProfiles;
    }

    public void setMergedProfiles(Set<String> mergedProfiles) {
        this.mergedProfiles = mergedProfiles;
    }

    public void setAttributes(List<Attribute> attributes) {
        if(attributes == null) {
            attributes = new ArrayList<Attribute>();
        }
        for (Attribute attribute : attributes) {
            attribute.setDirty(true);
        }
        this.attributes = attributes;
        setDirty(true);
    }
    public void setAttribute( String collectApp,String section , String name , Object value) {
        if(attributes == null) {
            attributes = new ArrayList<Attribute>();
        }
        Attribute attribute= new Attribute(collectApp,section);
        attribute.getData().put(name,value);
        this.attributes.add(attribute);
    }
    public void addSession(Session session){
        setDirty(true);
        session.setDirty(true);
        sessions.add(session);
    }

    public void resetDirty() {
        setDirty(false);
        for (Session session : sessions) {
            session.resetDirty();
        }
        for (Attribute attribute : attributes) {
            attribute.resetDirty();
        }
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }
}
