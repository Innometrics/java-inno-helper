package com.innometrics.integrationapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.innometrics.integrationapp.appsettings.FieldSetsEntry;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.Attribute;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Session;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import jdk.nashorn.internal.ir.ObjectNode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by killpack on 26.11.15.
 */
public class InnoTransformer {
    Map<String, RulesEntry> rulesEntries = new HashMap<>();
    public enum DataType {
        STRING,INTEGER,LONG,DOUBLE, DATE, TIMESTAMP
    }
    public enum DataLevel {
        EVENTDATA,SESIONDATA,ATTRIBUTEDATA, MACROS,STATIC, PROFILEID,PROFILECRETED, SESSIONCREATED,EVENTCREATED, EVENTDEFINITION
    }
    public static final String
            PROFILE_ATTRIBUTE = "profileAttribute",
            PROFILE_ID = "profileId",
            EVENT_VALUE = "eventValue",
            SESSION_VALUE = "sessionValue",
            STATIC_VALUE = "static",
            MACRO_VALUE = "macro",
            META_VALUE = "meta";
    static final String
            MARCO_TIMESTAMP_NOW = "timestamp_now",
            MARCO_TIMESTAMP_EVENT = "timestamp_event",
            MARCO_REQUEST_IP = "request_ip",
            MARCO_USER_AGENT = "user_agent";
    private static final String
            META_PROFILE_ID = "profileId",
            META_COMPANY_ID = "company_id",
            META_BUCKET_ID = "bucket_id",
            META_EVENT_DEFINITION_ID = "event_def_id",
            META_APP_SECTION_EVENT = "app_section_event",
            META_COLLECT_APP = "collect_app",
            META_SECTION = "section";

    public static final String MAPPING_SET_NAME = "mapping";


    public InnoTransformer(RulesEntry[] rulesEntries) {
        for (RulesEntry rulesEntry : rulesEntries) {
            this.rulesEntries.put(rulesEntry.getEvent(), rulesEntry);
        }
    }

    public InnoTransformer(InnoHelper innoHelper) throws ExecutionException, InterruptedException {
        this(innoHelper.getCustom("rules", RulesEntry[].class));
    }

    public Map<String, Object> fromProfile(Profile profile) {
        Map<String, Object> result = new HashMap<>();
        RulesEntry rulesEntry = rulesEntries.get(InnoHelperUtils.getFullFirstEventName(profile));
        if (rulesEntry == null) {
            return result;
        }
        QueueEvent queueEvent = new QueueEvent(profile);
        for (FieldSetsEntry fieldSet : rulesEntry.getFieldSets()) {
            String outGoingFieldName = fieldSet.getSetName();
            if (outGoingFieldName.equals(MAPPING_SET_NAME)) {
                for (FieldsEntry field : fieldSet.getFields()) {
                    if (field == null || field.getType() == null){
//                        result.put(field.getFieldName(), getValue(profile, field));
                }
            }
        }}
        return result;
    }

    public Profile toProfile(Map<String, Object> map) {
        Profile profile = new Profile();
        return profile;
    }

    private static Object getUserAttribute(List<Attribute> attributes, String valueLocation) {
        String[] layers = valueLocation.split("/");
        for (Attribute attribute : attributes) {
            if (attribute.getCollectApp().equals(layers[0]) && attribute.getSection().equals(layers[1])) {
                return attribute.getData().get(layers[2]);
            }
        }
        return null;
    }

}
