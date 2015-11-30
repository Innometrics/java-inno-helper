package com.innometrics.integrationapp.model;

import com.innometrics.integrationapp.utils.InnoHelperUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event extends Dirty {

    private String id = InnoHelperUtils.getRandomID(8);
    private Date createdAt;
    private String definitionId;
    private Map<String, Object> data = new HashMap<>();
    private transient Session session;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        setDirty(true);
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        setDirty(true);
        this.createdAt = createdAt;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        setDirty(true);
        this.definitionId = definitionId;
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    public void putData(String key, Object value) {
        setDirty(true);
        data.put(key, value);
    }

    public void setData(Map<String, Object> data) {
        setDirty(true);
        this.data = data;
    }

    void setSession(Session session) {
        this.session = session;
    }

    //todo add data equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
        if (session != null) {
            session.setDirty(true);
        }
    }
}
