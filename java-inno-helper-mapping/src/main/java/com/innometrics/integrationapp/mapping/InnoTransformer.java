package com.innometrics.integrationapp.mapping;


import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.FieldSetsEntry;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.mapping.adapters.InnAdapter;
import com.innometrics.integrationapp.model.*;
import com.innometrics.integrationapp.utils.InnoHelperUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by killpack on 29.07.15.
 */
public class InnoTransformer {
    public static final String RULES = "rules";
    public static final String MAPPING_SET_NAME = "mapping";
    Map<String, RulesEntry> rulesEntries = new ConcurrentHashMap<>();
    InnoHelper innoHelper;

    public InnoTransformer(InnoHelper innoHelper) throws IOException {
        this.innoHelper = innoHelper;
        RulesEntry[] rulesEntries = this.innoHelper.getCustom(RULES, RulesEntry[].class);
        for (RulesEntry rulesEntry : rulesEntries) {
            this.rulesEntries.put(rulesEntry.getEvent(), rulesEntry);
        }
    }

    public Map<String, Object> fromProfileStream(ProfileStreamMessage profileStreamMessage) throws MappingDataException {
        Profile profile = profileStreamMessage.getProfile();
        Map<String, Object> result = new HashMap<>();
        RulesEntry rulesEntry = rulesEntries.get(InnoHelperUtils.getFullFirstEventName(profile));
        if (rulesEntry == null) {
            return result;
        }
        for (FieldsEntry field : getMapping(rulesEntry)) {
            if (field != null && field.getType() != null) {
                String stringType = field.getType();
                if (!stringType.isEmpty()) {
                    DataLevel type = DataLevel.valueOf(stringType.toUpperCase());
                    InnAdapter adapter = type.getAdapter();
                    adapter.setInnoHelper(innoHelper);
                    result.put(field.getFieldName(), adapter.getValueFromPS(profileStreamMessage, field));
                }
            }
        }
        return result;
    }

//    public Profile toProfile(Map<String, Object> map, String entryName) {
//        for (RulesEntry entry : rulesEntries.values()) {
//            if (entry != null && entry.getName().equals(entryName)) {
//                return getProfile(map, entry);
//            }
//        }
//        return null;
//    }
//
//    private Profile getProfile(Map<String, Object> map, RulesEntry rulesEntry) {
//        for (FieldSetsEntry setsEntry : rulesEntry.getFieldSets()) {
//            if (MAPPING_SET_NAME.equals(setsEntry.getSetName())) {
//                return getProfileBySetEntry(map, setsEntry);
//            }
//        }
//        return null;
//    }

    public List<FieldsEntry> getMapping(RulesEntry rulesEntry) {
        for (FieldSetsEntry fieldSet : rulesEntry.getFieldSets()) {
            String outGoingFieldName = fieldSet.getSetName();
            if (outGoingFieldName.equals(MAPPING_SET_NAME)) {
                return fieldSet.getFields();
            }
        }
        return new ArrayList<>();
    }
//    private Profile getProfileBySetEntry(Map<String, Object> map, FieldSetsEntry setsEntry) {
//        Profile profile = new Profile();
//        Session session = new Session();
//        Event event = new Event();
//        session.addEvent(event);
//        profile.addSession(session);
//        for (FieldsEntry fieldsEntry : setsEntry.getFields()) {
//            fillProfileByEntry(profile, fieldsEntry, map);
//        }
//
//        return profile;
//    }

//    void fillProfileByEntry(Profile profile, FieldsEntry fieldsEntry, Map<String, Object> map) {
//        String type = fieldsEntry.getType();
//        DataLevel dataLevel = DataLevel.valueOf(type.toUpperCase());
//        if (dataLevel == null) {
//            return;
//        }
//        Session session = profile.getSessions().get(0);
//        Event event = session.getEvents().get(0);
//        switch (dataLevel) {
//            case PROFILE_ID: {
//                profile.setId((String) convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry));
//                break;
//            }
//            case ATTRIBUTE_DATA: {
//                String[] valueRef = validateValueRef(fieldsEntry.getValueRef());
//                profile.setAttribute(valueRef[0], valueRef[1], valueRef[2], InnoHelperUtils.getGson().toJsonTree(convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry)));
//                break;
//            }
//            case EVENT_DATA: {
//                String[] valueRef = validateValueRef(fieldsEntry.getValueRef());
//                event.putData(valueRef[0], InnoHelperUtils.getGson().toJsonTree(convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry)));
//                break;
//            }
//            case EVENT_DEFINITION: {
//                event.setDefinitionId((String) convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry));
//                break;
//            }
//            case SESSION_DATA: {
//                String[] valueRef = validateValueRef(fieldsEntry.getValueRef());
//                session.setCollectApp(valueRef[0]);
//                session.setSection(valueRef[1]);
//                session.putData(valueRef[2], InnoHelperUtils.getGson().toJsonTree(convertValue(map.get(fieldsEntry.getFieldName()), fieldsEntry)));
//                break;
//            }
//            case SESSION_CREATED: {
//                session.setCreatedAt((Date) map.get(fieldsEntry.getFieldName()));
//                break;
//            }
//            case EVENT_CREATED: {
//                event.setCreatedAt((Date) map.get(fieldsEntry.getFieldName()));
//                break;
//            }
//            case PROFILE_CREATED: {
//                profile.setCreatedAt((Date) map.get(fieldsEntry.getFieldName()));
//                break;
//            }
//        }
//    }

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


}
