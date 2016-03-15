package com.innometrics.integrationapp.model;

/**
 * Created by killpack on 15.03.16.
 */
public class Meta {
    String eventListenerId;
    boolean isNew;
    int profileSize;
    RequestMeta requestMeta;

    public String getEventListenerId() {
        return eventListenerId;
    }

    public void setEventListenerId(String eventListenerId) {
        this.eventListenerId = eventListenerId;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public int getProfileSize() {
        return profileSize;
    }

    public void setProfileSize(int profileSize) {
        this.profileSize = profileSize;
    }

    public RequestMeta getRequestMeta() {
        return requestMeta;
    }

    public void setRequestMeta(RequestMeta requestMeta) {
        this.requestMeta = requestMeta;
    }

}
