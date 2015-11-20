package com.innometrics.integrationapp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class Session {

    private String id;
    private Date createdAt;
    private String collectApp;
    private String section;
    private Map<String, Object> data;
    private List<Event> events = new ArrayList<Event>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return collectApp.equals(session.collectApp) && createdAt.equals(session.createdAt) &&
                !(data != null ? !data.equals(session.data) : session.data != null) &&
                events.equals(session.events) &&
                id.equals(session.id) &&
                section.equals(session.section);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + collectApp.hashCode();
        result = 31 * result + section.hashCode();
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + events.hashCode();
        return result;
    }

//    public int compareTo(Session o) {
//        if(o == null) {
//            throw new NullPointerException();
//        } else if(this.equals(o)) {
//            return 0;
//        } else if(createdAt.before(o.createdAt)) {
//            return -1;
//        } else if(createdAt.after(o.createdAt)) {
//            return 1;
//        } else {
//            return 0;
//        }
//    }
}
