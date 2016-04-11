package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.ProfileStreamMessage;

import java.util.Date;
import java.util.Map;


/**
 * Created by killpack on 06.04.16.
 */
public class ProfileCreatedAdapter extends InnAdapter {

    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        return convertValue(profileStreamHelper.getProfile(profileStreamMessage).getCreatedAt(), fieldsEntry);
    }

    @Override
    public void setValueToProfile(Profile profile, FieldsEntry fieldsEntry, Map<String, Object> map) throws MappingDataException {
        Object date = getValue(fieldsEntry, map);
        if (date instanceof Date) {
            profile.setCreatedAt((Date) date);
        }else {
            throw new MappingDataException("Profile createrdAt in field "+ fieldsEntry.getFieldName()+"must be DATE convert type");
        }
    }


}
