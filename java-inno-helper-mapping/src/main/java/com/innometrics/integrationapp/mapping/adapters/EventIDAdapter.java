package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.ProfileStreamMessage;

import java.util.Map;


/**
 * Created by killpack on 06.04.16.
 */
public class EventIDAdapter extends InnAdapter {

    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        return convertValue(profileStreamHelper.getEvent(profileStreamMessage).getId(), fieldsEntry);
    }

    @Override
    public void setValueToProfile(Profile profile, FieldsEntry fieldsEntry, Map<String, Object> map) throws MappingDataException {
        Object val = getValue(fieldsEntry, map);
        if (val instanceof String){
            profile.getSessions().get(0).getEvents().get(0).setId((String) val);
        }else {
            throw new MappingDataException("Event id in field ["+ fieldsEntry.getFieldName()+"] must be STRING convert type");
        }
    }


}
