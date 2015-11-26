package com.innometrics.integrationapp;

import java.util.Map;

/**
 * Created by killpack on 26.11.15.
 */
@Deprecated
public class CrossSystemMessage {
    private String id;
    private String appId;
    private String url;
    private String companyId;
    private String bucketId;
    private String appRuleId;
    private String eventId;
    private String profileId;
    private Map<String ,Object> fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getAppRuleId() {
        return appRuleId;
    }

    public void setAppRuleId(String appRuleId) {
        this.appRuleId = appRuleId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }
}
