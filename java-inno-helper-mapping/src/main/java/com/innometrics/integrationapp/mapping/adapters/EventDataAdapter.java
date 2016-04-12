package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.ProfileStreamMessage;

import java.util.Map;


/**
 * Created by killpack on 06.04.16.
 */
public class EventDataAdapter extends InnAdapter {
    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        String valueRef = getValueRef(fieldsEntry);
        Event event = profileStreamHelper.getEvent(profileStreamMessage);
        if (event == null || event.getData() == null || !event.getData().containsKey(valueRef)) {
            throw new MappingDataException("Profile not contain data [EventData/" + valueRef + "]");
        } else {
            return convertValue(event.getData().get(valueRef), fieldsEntry);
        }
    }

    @Override
    public void setValueToProfile(Profile profile, FieldsEntry fieldsEntry, Map<String, Object> map) throws MappingDataException {
        profile.getSessions().get(0).getEvents().get(0).putData(fieldsEntry.getValueRef(), getDataAsJson(getValue(fieldsEntry, map)));
    }

}
