package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.InnAdapter;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.ProfileStreamMessage;


/**
 * Created by killpack on 06.04.16.
 */
public class EventCreatedAdapter extends InnAdapter {

    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        return convertValue(profileStreamHelper.getEvent(profileStreamMessage).getCreatedAt(), fieldsEntry);
    }

    @Override
    public void setValueToProfile() {

    }


}
