package com.innometrics.integrationapp.model;

import java.util.Date;
import java.util.Map;

public class Event{
    private String id;
    private Date createdAt;
    private String definitionId;
    private Map<String,Object> data;

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

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
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

//    public int compareTo(Event o) {
//        if(o == null) {
//            throw new NullPointerException();
//        } else if(this.equals(o)) {
//            return 0;
//        } else if(this.getCreatedAt().before(o.getCreatedAt())) {
//            return -1;
//        } else if(this.getCreatedAt().after(o.getCreatedAt())) {
//            return 1;
//        } else {
//            return 0;
//        }
//    }
}
