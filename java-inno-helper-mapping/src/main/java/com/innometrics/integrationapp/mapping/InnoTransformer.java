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
        RulesEntry[] rulesEntriesFromHelper = this.innoHelper.getCustom(RULES, RulesEntry[].class);
        for (RulesEntry rulesEntry : rulesEntriesFromHelper) {
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

    public Profile toProfile(Map<String, Object> map, String entryName) throws MappingDataException {
        for (RulesEntry entry : rulesEntries.values()) {
            if (entry != null && entry.getName().equals(entryName)) {
                return getProfile(map, entry);
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
