package com.innometrics.integrationapp.model;

import com.innometrics.integrationapp.utils.InnoHelperUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class Session  extends Dirty {

    private String id  = InnoHelperUtils.getRandomID(8);
    private Date createdAt = new Date();
    private String collectApp =StringUtils.EMPTY;
    private String section = StringUtils.EMPTY;
    private Map<String, Object> data = new HashMap<>();
    private List<Event> events = new ArrayList<Event>();

    public String getId() {
        setDirty(true);
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        setDirty(true);
        this.createdAt = createdAt;
    }

    public String getCollectApp() {

        return collectApp;
    }

    public void setCollectApp(String collectApp) {
        setDirty(true);
        this.collectApp = collectApp;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        setDirty(true);
        this.section = section;
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    public void setData(Map<String, Object> data) {
        setDirty(true);
        this.data = data;
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void setEvents(List<Event> events) {
        setDirty(true);
        this.events = events;
    }

    public void addEvent(Event... events ) {
        setDirty(true);
        for (Event event : events) {
            event.setSession(this);
        }
        Collections.addAll(this.events, events);
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

    public void resetDirty() {
        setDirty(false);
        for (Event event : events) {
            event.setDirty(false);
        }
    }

    public void putData(String fieldName, Object o) {
        setDirty(true);
        data.put(fieldName,o);
    }
}
