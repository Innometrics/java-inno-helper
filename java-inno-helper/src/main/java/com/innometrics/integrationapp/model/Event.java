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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (createdAt != null ? !createdAt.equals(event.createdAt) : event.createdAt != null) return false;
        if (data != null ? !data.equals(event.data) : event.data != null) return false;
        if (definitionId != null ? !definitionId.equals(event.definitionId) : event.definitionId != null) return false;
        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (definitionId != null ? definitionId.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
        if (session != null) {
            session.setDirty(true);
        }
    }
}
