package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.ProfileStreamMessage;


/**
 * Created by killpack on 06.04.16.
 */
public class EventDataAdapter extends InnAdapter {
    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        String valueRef = getValueRef(fieldsEntry);
        Event event = profileStreamHelper.getEvent(profileStreamMessage);
        if (event == null || event.getData() == null || !event.getData().containsKey(valueRef)) {
            throw new MappingDataException("Profile not contain data [EventData/" + valueRef+"]");
        } else {
            return convertValue(event.getData().get(valueRef), fieldsEntry);
        }
    }

    @Override
    public void setValueToProfile() {

    }


}
