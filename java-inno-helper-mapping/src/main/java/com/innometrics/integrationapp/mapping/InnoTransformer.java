package com.innometrics.integrationapp.mapping;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.FieldSetsEntry;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.*;
import com.innometrics.integrationapp.utils.InnoHelperUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by killpack on 29.07.15.
 */
public class InnoTransformer {
    public static final String RULES = "rules";
    public static final String MAPPING_SET_NAME = "mapping";
    public static final String USER_AGENT = "User-Agent";
    Map<String, RulesEntry> rulesEntries = new ConcurrentHashMap<>();
    InnoHelper innoHelper;

    public InnoTransformer(InnoHelper innoHelper) throws Exception {
        this.innoHelper = innoHelper;
        RulesEntry[] rulesEntries = innoHelper.getCustom(RULES, RulesEntry[].class);
        for (RulesEntry rulesEntry : rulesEntries) {
            this.rulesEntries.put(rulesEntry.getEvent(), rulesEntry);
        }
    }

    public Map<String, Object> fromProfileStream(ProfileStreamMessage profileStreamMessage) throws ProfileDataException {
        Profile profile = profileStreamMessage.getProfile();
        Map<String, Object> result = new HashMap<>();
        RulesEntry rulesEntry = rulesEntries.get(InnoHelperUtils.getFullFirstEventName(profile));
        if (rulesEntry == null) {
            return result;
        }
        for (FieldsEntry field : getMapping(rulesEntry)) {
            if (field != null && field.getType() != null) {
                result.put(field.getFieldName(), getValue(profileStreamMessage, field));
            }
        }
        return result;
    }

    public List<FieldsEntry> getMapping(RulesEntry rulesEntry) {
        for (FieldSetsEntry fieldSet : rulesEntry.getFieldSets()) {
            String outGoingFieldName = fieldSet.getSetName();
            if (outGoingFieldName.equals(MAPPING_SET_NAME)) {
                return fieldSet.getFields();
            }
        }
        return new ArrayList<>();
    }

    public Profile toProfile(Map<String, Object> map, String entryName) {
        for (RulesEntry entry : rulesEntries.values()) {
            if (entry != null && entry.getName().equals(entryName)) {
                return getProfile(map, entry);
            }
        }
        return null;
    }

    private Profile getProfile(Map<String, Object> map, RulesEntry rulesEntry) {
        for (FieldSetsEntry setsEntry : rulesEntry.getFieldSets()) {
            if (MAPPING_SET_NAME.equals(setsEntry.getSetName())) {
                return getProfileBySetEntry(map, setsEntry);
            }
        }
        return null;
    }

    private Profile getProfileBySetEntry(Map<String, Object> map, FieldSetsEntry setsEntry) {
        Profile profile = new Profile();
        Session session = new Session();
        Event event = new Event();
        session.addEvent(event);
        profile.addSession(session);
        for (FieldsEntry fieldsEntry : setsEntry.getFields()) {
            fillProfileByEntry(profile, fieldsEntry, map);
        }

        return profile;
    }

    void fillProfileByEntry(Profile profile, FieldsEntry fieldsEntry, Map<String, Object> map) {
        String type = fieldsEntry.getType();
        DataLevel dataLevel = DataLevel.valueOf(type.toUpperCase());
        if (dataLevel == null) {
            return;
        }
        Session session = profile.getSessions().get(0);
        Event event = session.getEvents().get(0);
        switch (dataLevel) {
            case PROFILE_ID: {
                profile.setId((String) convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry));
                break;
            }
            case ATTRIBUTE_DATA: {
                String[] valueRef = validateValueRef(fieldsEntry.getValueRef());
                profile.setAttribute(valueRef[0], valueRef[1], valueRef[2], InnoHelperUtils.getGson().toJsonTree(convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry)));
                break;
            }
            case EVENT_DATA: {
                String[] valueRef = validateValueRef(fieldsEntry.getValueRef());
                event.putData(valueRef[0], InnoHelperUtils.getGson().toJsonTree(convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry)));
                break;
            }
            case EVENT_DEFINITION: {
                event.setDefinitionId((String) convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry));
                break;
            }
            case SESSION_DATA: {
                String[] valueRef = validateValueRef(fieldsEntry.getValueRef());
                session.setCollectApp(valueRef[0]);
                session.setSection(valueRef[1]);
                session.putData(valueRef[2], InnoHelperUtils.getGson().toJsonTree(convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry)));
                break;
            }
            case SESSION_CREATED: {
                session.setCreatedAt((Date) map.get(fieldsEntry.getFieldName()));
                break;
            }
            case EVENT_CREATED: {
                event.setCreatedAt((Date) map.get(fieldsEntry.getFieldName()));
                break;
            }
            case PROFILE_CREATED: {
                profile.setCreatedAt((Date) map.get(fieldsEntry.getFieldName()));
                break;
            }
        }
    }

    private String[] validateValueRef(String valueRef) {
        if (valueRef == null || valueRef.isEmpty()) {
            // todo
            throw new IllegalArgumentException("");
        }
        String[] res = valueRef.split("/");
        if (!(res.length == 3 || res.length == 1)) {
            throw new IllegalArgumentException("");
        }
        return res;
    }


    public Object getValue(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws ProfileDataException {
        Profile profile = profileStreamMessage.getProfile();
        Object result = null;
        String stringType = fieldsEntry.getType();
        if (stringType == null || stringType.isEmpty()) return null;
        DataLevel type = DataLevel.valueOf(stringType.toUpperCase());
        Session session = profile.getSessions().get(0);
        String valueRef = fieldsEntry.getValueRef();
        switch (type) {
            case EVENT_DATA: {
                result = getObject(session, valueRef);
                break;
            }
            case SESSION_DATA: {
                result = getSessionData(session, valueRef);
                break;
            }
            case ATTRIBUTE_DATA: {
                result = getAttributeData(profile,  valueRef);
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
            case SESSION_ID: {
                result = session.getId();
                break;
            }
            case EVENT_CREATED: {
                result = session.getEvents().get(0).getCreatedAt();
                break;
            }
            case EVENT_ID: {
                result = session.getEvents().get(0).getId();
                break;
            }
            case EVENT_DEFINITION: {
                result = session.getEvents().get(0).getDefinitionId();
                break;
            }
            case MACRO: {
                result = getMacro(profileStreamMessage, fieldsEntry.getValueRef());
                break;
            }
            case META: {
                result = getMeta(profile, fieldsEntry.getValueRef());
                break;
            }
        }
        return convertValue(result, fieldsEntry);
    }

    private Object getAttributeData(Profile profile,  String valueRef) {
        String[] tmp = valueRef.split("/");
        for (Attribute attribute : profile.getAttributes()) {
            if (attribute.getCollectApp().equals(tmp[0]) && attribute.getSection().equals(tmp[1])) {
                return attribute.getData().get(tmp[2]);
            }
        }
        return null;
    }

    private Object getSessionData(Session session, String valueRef) throws ProfileDataException {
        Object result;
        if (session.getData() != null && session.getData().containsKey(valueRef)) {
            result = session.getData().get(valueRef);
        } else throw new ProfileDataException("SessionData/" + valueRef);
        return result;
    }

    private Object getObject(Session session, String valueRef) throws ProfileDataException {
        Object result;Map<String, JsonElement> data;
        data = session.getEvents().get(0).getData();
        if (data != null && data.containsKey(valueRef)) {
            result = data.get(valueRef);
        } else throw new ProfileDataException("EventData/" + valueRef);
        return result;
    }

    private Object getMacro(ProfileStreamMessage profileStreamMessage, String valueRef) {
        Macro macro = Macro.valueOf(valueRef.toUpperCase());
        switch (macro) {
            case CURRENT_TIMESTAMP: {
                return System.currentTimeMillis();
            }
            case REQUEST_IP: {
                if (profileStreamMessage.getMeta() != null)
                    return profileStreamMessage.getMeta().getRequestMeta().getRequestIp();
            }
            case USER_AGENT: {
                if (profileStreamMessage.getMeta() != null)
                    return profileStreamMessage.getMeta().getRequestMeta().getHeaders().get(USER_AGENT);
            }
        }
        return null;
    }

    private Object getMeta(Profile profile, String valueRef) {
        MetaConstant metaConstant = MetaConstant.valueOf(valueRef.toUpperCase());
        switch (metaConstant) {
            case BUCKET_ID: {
                return innoHelper.getBucketId();
            }
            case COMPANY_ID: {
                return innoHelper.getCompanyId();
            }
            case COLLECTAPP: {
                return profile.getSessions().get(0).getCollectApp();
            }
            case SECTION: {
                return profile.getSessions().get(0).getSection();
            }
        }
        return null;
    }

    protected Object convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value == null || value.equals("null")) return null;
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
        if (value instanceof JsonElement && DataType.JSON.equals(type)) {
            return value;
        }
        switch (type) {
            case JSON: {
                return value;
            }
            case DOUBLE: {
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                return Double.valueOf(getAssString(value));
            }
            case INTEGER: {
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
                return Long.valueOf(getAssString(value));
            }
            case STRING: {
                return getAssString(value);
            }
            case TIMESTAMP: {
                if (value instanceof Long) return value;
                if (value instanceof Date) return ((Date) value).getTime();
                String tmp = getAssString(value);
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
            case DATE: {
                if (value instanceof Date) return value;
                if (value instanceof Long) return new Date((Long) value);
                String tmp = getAssString(value);
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

    String getAssString(Object o) {
        return o instanceof JsonPrimitive ? ((JsonPrimitive) o).getAsString() : String.valueOf(o);
    }
}
