package com.innometrics.integrationapp.mapping;


import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.FieldSetsEntry;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.Attribute;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Session;
import com.innometrics.integrationapp.utils.InnoHelperUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by killpack on 29.07.15.
 */
public class InnoTransformer {
    public static final String RULES = "rules";
    public static final String MAPPING_SET_NAME = "mapping";
    Map<String, RulesEntry> rulesEntries = new HashMap<>();
    InnoHelper innoHelper ;
    public InnoTransformer(InnoHelper  innoHelper) throws ExecutionException, InterruptedException, IOException {
        this.innoHelper = innoHelper;
        RulesEntry [] rulesEntries = innoHelper.getCustom(RULES,RulesEntry[].class);
        for (RulesEntry rulesEntry : rulesEntries) {
            this.rulesEntries.put(rulesEntry.getEvent(), rulesEntry);
        }
    }

    public Map<String, Object> fromProfile(Profile profile) throws ProfileDataException {
        Map<String, Object> result = new HashMap<>();
        RulesEntry rulesEntry = rulesEntries.get(InnoHelperUtils.getFullFirstEventName(profile));
        if (rulesEntry == null) {
            return result;
        }
        for (FieldSetsEntry fieldSet : rulesEntry.getFieldSets()) {
            String outGoingFieldName = fieldSet.getSetName();
            if (outGoingFieldName.equals(MAPPING_SET_NAME)) {
                for (FieldsEntry field : fieldSet.getFields()) {
                    if (field != null && field.getType() != null) {
                        result.put(field.getFieldName(), getValue(profile, field));
                    }
                }
            }
        }
        return result;
    }

    public Profile toProfile(Map<String, Object> map, String entryName) {
        RulesEntry rulesEntry = null;
        for (RulesEntry entry : rulesEntries.values()) {
            if (entry.getName().equals(entryName)){
                rulesEntry = entry;
            }
        }
        for (FieldSetsEntry setsEntry : rulesEntry.getFieldSets()) {
            if(MAPPING_SET_NAME.equals(setsEntry.getSetName())){
                Profile profile = new Profile();
                Session session= new Session();
                Event event = new Event();
                for (FieldsEntry fieldsEntry : setsEntry.getFields()) {
                    String type = fieldsEntry.getType();
                    DataLevel dataLevel = DataLevel.valueOf(type.toUpperCase());
                    if (dataLevel!=null){
                        switch (dataLevel){
                            case PROFILE_ID:{
                                profile.setId((String) convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry));
                                break;
                            }
                            case ATTRIBUTE_DATA:{
                                String[] valueRef= validateValueRef(fieldsEntry.getValueRef());
                                profile.setAttribute(valueRef[0], valueRef[1], valueRef[2], convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry));
                                break;
                            }
                            case EVENT_DATA:{
                                String[] valueRef= validateValueRef(fieldsEntry.getValueRef());
                                event.putData(valueRef[0],convertValue(map.get(fieldsEntry.getFieldName()),fieldsEntry));
                                break;
                            }
                            case EVENT_DEFINITION:{
                                event.setDefinitionId((String) convertValue(map.get(fieldsEntry.getFieldName()),fieldsEntry));
                                break;
                            }
                            case SESSION_DATA:{
                                String[] valueRef= validateValueRef(fieldsEntry.getValueRef());
                                session.setCollectApp(valueRef[0]);
                                session.setSection(valueRef[1]);
                                session.putData(valueRef[2],convertValue(map.get(fieldsEntry.getFieldName()),fieldsEntry));
                                break;
                            }
                            case SESSION_CREATED:{
                                session.setCreatedAt((Date) convertValue(map.get(fieldsEntry.getFieldName()),fieldsEntry));
                                break;
                            }
                            case EVENT_CREATED:{
                                event.setCreatedAt((Date) convertValue(map.get(fieldsEntry.getFieldName()),fieldsEntry));
                                break;
                            }case PROFILE_CREATED:{
                                profile.setCreatedAt((Date) convertValue(map.get(fieldsEntry.getFieldName()),fieldsEntry));
                                break;
                            }
                        }
                    }
                }

                session.addEvent(event);
                profile.addSession(session);
                return profile;
            }
        }
        return null;
    }

    private String[] validateValueRef(String valueRef) {
        if (valueRef==null ||valueRef.isEmpty()){
            // todo
            throw new IllegalArgumentException("");
        }
        String[] res = valueRef.split("/");
        if (!(res.length==3 || res.length==1 )){
            throw new IllegalArgumentException("");
        }
        return res;
    }


    public Object getValue(Profile profile, FieldsEntry fieldsEntry) throws ProfileDataException {
        Object result = null;
        String stringType = fieldsEntry.getType();
        if (stringType == null || stringType.isEmpty()) return null;
        DataLevel type = DataLevel.valueOf(stringType.toUpperCase());
        Session session = profile.getSessions().get(0);
        String valueRef = fieldsEntry.getValueRef();
        Map<String, Object> data;
        switch (type) {
            case EVENT_DATA: {
                data = session.getEvents().get(0).getData();
                if (data != null && data.containsKey(valueRef)) {
                    result = session.getEvents().get(0).getData().get(valueRef);
                } else throw new ProfileDataException("EventData/" + valueRef);
                break;
            }
            case SESSION_DATA: {
                data = session.getEvents().get(0).getData();
                if (data != null && data.containsKey(valueRef)) {
                    result = session.getData().get(valueRef);
                } else throw new ProfileDataException("SessionData/" + valueRef);
                break;
            }
            case ATTRIBUTE_DATA: {
                String[] tmp = valueRef.split("/");
                for (Attribute attribute : profile.getAttributes()) {
                    if (attribute.getCollectApp().equals(tmp[0]) && attribute.getSection().equals(tmp[1])) {
                        result = attribute.getData().get(tmp[2]);
                    }
                }
                break;
            }
            case STATIC: {
                result = fieldsEntry.getValueRef();
                break;
            }
            case PROFILE_ID: {
                result = profile.getId();
                break;
            }
            case PROFILE_CREATED: {
                result = profile.getCreatedAt().toString();
                break;
            }
            case SESSION_CREATED: {
                result = session.getCreatedAt().toString();
                break;
            }
            case EVENT_CREATED: {
                result = session.getEvents().get(0).getCreatedAt().toString();
                break;
            }
            case EVENT_DEFINITION: {
                result = session.getEvents().get(0).getDefinitionId();
                break;
            }
            case MACROS: {
//                result = fieldsEntry.a();// todo implementMacros
                break;
            }
        }
        return convertValue(result, fieldsEntry);
    }

    protected static Object convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value == null || value.equals("null")) return null;
        String tmp = String.valueOf(value);
        DataType type = DataType.STRING;
        Map<String, Object> settings = null;
        if (fieldsEntry != null && fieldsEntry.getFieldSettings() != null) {
            try {
                settings = fieldsEntry.getFieldSettings();
                type = DataType.valueOf(String.valueOf(settings.get("convertType")).toUpperCase());
            } catch (IllegalArgumentException e) {
//                logger.debug(e.getMessage());
            }
        }
        switch (type) {
            case DOUBLE: {
                return Double.valueOf(tmp);
            }
            case INTEGER: {
                return Integer.valueOf(tmp);
            }
            case TIMESTAMP: {
                if (settings.isEmpty()) return Long.valueOf(tmp);
                String format = String.valueOf(settings.get("timeFormat"));
                if (format != null && !format.isEmpty()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                    try {
                        return simpleDateFormat.parse(tmp).getTime();
                    } catch (ParseException e) {
                        return Long.valueOf(tmp);
                    }
                }
                break;
            }
            case LONG: {
                return Long.valueOf(tmp);
            }
            case STRING: {
                return value;
            }
            case DATE: {
                if (settings.isEmpty()) return null;
                String format = String.valueOf(settings.get("timeFormat"));
                if (format != null && !format.isEmpty()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                    try {
                        return simpleDateFormat.parse(tmp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                break;
            }
        }
        return value;
    }
}
