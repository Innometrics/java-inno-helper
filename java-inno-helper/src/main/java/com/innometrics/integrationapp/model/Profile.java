package com.innometrics.integrationapp.model;

import com.google.gson.JsonElement;

import java.util.*;


public class Profile extends Dirty {

    private String id;
    private String version = "1.0";
    private Date createdAt;

    private List<Session> sessions = new ArrayList<Session>();
    private List<Attribute> attributes = new ArrayList<Attribute>();
    private Set<String> mergedProfiles = new LinkedHashSet();

    public Profile() {
    }

    public Profile(String profileId) {
        id= profileId;
    }

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
    public void setAttribute( String collectApp,String section , String name , JsonElement value) {
        if(attributes == null) {
            attributes = new ArrayList<Attribute>();
        }
        Attribute attribute= new Attribute(collectApp,section);
        attribute.putData(name,value);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        if (attributes != null ? !attributes.equals(profile.attributes) : profile.attributes != null) return false;
        if (createdAt != null ? !createdAt.equals(profile.createdAt) : profile.createdAt != null) return false;
        if (id != null ? !id.equals(profile.id) : profile.id != null) return false;
        if (mergedProfiles != null ? !mergedProfiles.equals(profile.mergedProfiles) : profile.mergedProfiles != null)
            return false;
        if (sessions != null ? !sessions.equals(profile.sessions) : profile.sessions != null) return false;
        if (version != null ? !version.equals(profile.version) : profile.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (sessions != null ? sessions.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (mergedProfiles != null ? mergedProfiles.hashCode() : 0);
        return result;
    }


}
