package com.innometrics.integrationapp.mapping;


import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.FieldSetsEntry;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.mapping.adapters.InnAdapter;
import com.innometrics.integrationapp.model.*;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by killpack on 29.07.15.
 */
public class InnoTransformer {
    public static final String RULES = "rules";
    public static final String MAPPING_SET_NAME = "mapping";
    private static final Logger LOGGER = Logger.getLogger(InnoTransformer.class);
    Map<String, List<RulesEntry>> rulesEntries = new ConcurrentHashMap<>();
    InnoHelper innoHelper;

    public InnoTransformer(InnoHelper innoHelper) throws IOException {
        this.innoHelper = innoHelper;
        RulesEntry[] rulesEntriesFromHelper = this.innoHelper.getCustom(RULES, RulesEntry[].class);
        for (RulesEntry rulesEntry : rulesEntriesFromHelper) {
            List<RulesEntry> tmp = rulesEntries.get(rulesEntry.getEvent());
            if (tmp == null) {
                tmp = new ArrayList<>();
                this.rulesEntries.put(rulesEntry.getEvent(), tmp);
            }
            tmp.add(rulesEntry);
        }
    }

    public Map<RulesEntry,Map<String, Object>> fromProfileStreamMultiRule(ProfileStreamMessage profileStreamMessage) throws MappingDataException {
        Map<RulesEntry,Map<String, Object>> result = new HashMap<>();
        List<RulesEntry> rulesEntrys = rulesEntries.get(InnoHelperUtils.getFullFirstEventName(profileStreamMessage.getProfile()));
        for (RulesEntry entry : rulesEntrys) {
            Map<String, Object> tmp = fromProfileStream(entry, profileStreamMessage);
            if (!tmp.isEmpty()){
                result.put(entry, tmp);
            }
        }
        return result;
    }




    Map<String, Object> fromProfileStream(RulesEntry rulesEntry, ProfileStreamMessage profileStreamMessage) throws MappingDataException {
        Map<String, Object> result = new HashMap<>();
//        RulesEntry rulesEntry = rulesEntries.get(InnoHelperUtils.getFullFirstEventName(profile));
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

    //temporary fix all data in profile
    public Map<RulesEntry,Map<String, Object>> fromFullProfileStreamMultiRule(ProfileStreamMessage profileStreamMessage) throws MappingDataException {
        Map<RulesEntry,Map<String, Object>> result = new HashMap<>();
        List<RulesEntry> rulesEntrys = rulesEntries.get(InnoHelperUtils.getFullFirstEventName(profileStreamMessage.getProfile()));
        for (RulesEntry entry : rulesEntrys) {
            Map<String, Object> tmp = fromFullProfile(entry, profileStreamMessage);
            if (!tmp.isEmpty()){
                result.put(entry, tmp);
            }
        }
        return result;
    }

    public Map<String, Object> fromFullProfile(RulesEntry rulesEntry,ProfileStreamMessage profileStreamMessage) throws MappingDataException {
        Map<String, Object> dataMap = null;
        try {
            dataMap = fromProfileStream(rulesEntry,profileStreamMessage);
            if (!validateData(dataMap)) {
                Profile profile = innoHelper.getProfile(profileStreamMessage.getProfile().getId());
                List<Session> session = new ArrayList();
                session.add(getSingleSession(profileStreamMessage.getProfile(), profile));
                profile.setSessions(session);
                profileStreamMessage.setProfile(profile);
                dataMap = fromProfileStream(rulesEntry,profileStreamMessage);
            }
        } catch (IOException | MappingDataException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return dataMap;
    }

    Session getSingleSession(Profile src, Profile fullProfile) {
        Session srcSession = src.getSessions().get(0);
        Event srcEvent = srcSession.getEvents().get(0);
        for (Session session : fullProfile.getSessions()) {
            if (session.getId().equals(srcSession.getId())) {
                for (Event event : session.getEvents()) {
                    if (event.getId().equals(srcEvent.getId())) {
                        List<Event> eventList = new ArrayList<>();
                        eventList.add(event);
                        session.setEvents(eventList);
                        return session;
                    }
                }
            }
        }
        return null;
    }

    boolean validateData(Map<String, Object> dataMap) {
        for (Object o : dataMap.values()) {
            if (o == null) return false;
        }
        return true;
    }

    //End of temporary fix all data in profile
    public Profile toProfile(Map<String, Object> map, String entryName) throws MappingDataException {
        for (List<RulesEntry> entrys : rulesEntries.values()) {
            if (entrys != null && !entrys.isEmpty() && entrys.get(0).getName().equals(entryName)) {
                return getProfile(map, entrys.get(0));
            }
        }
        return null;
    }

    private Profile getProfile(Map<String, Object> map, RulesEntry rulesEntry) throws MappingDataException {
        for (FieldSetsEntry setsEntry : rulesEntry.getFieldSets()) {
            if (MAPPING_SET_NAME.equals(setsEntry.getSetName())) {
                return getProfileBySetEntry(map, setsEntry);
            }
        }
        return null;
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

    private Profile getProfileBySetEntry(Map<String, Object> map, FieldSetsEntry setsEntry) throws MappingDataException {
        Profile profile = new Profile();
        Session session = new Session();
        Event event = new Event();
        session.addEvent(event);

        profile.addSession(session);
        for (FieldsEntry fieldsEntry : setsEntry.getFields()) {
            String stringType = fieldsEntry.getType();
            if (!stringType.isEmpty()) {
                DataLevel type = DataLevel.valueOf(stringType.toUpperCase());
                InnAdapter adapter = type.getAdapter();
                adapter.setValueToProfile(profile, fieldsEntry, map);
            }
        }

        return profile;
    }

}
