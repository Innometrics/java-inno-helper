package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.InnAdapter;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.Attribute;
import com.innometrics.integrationapp.model.ProfileStreamMessage;

import java.util.List;


/**
 * Created by killpack on 06.04.16.
 */
public class StaticAdapter extends InnAdapter {

    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        return convertValue(getValueRef(fieldsEntry), fieldsEntry);
    }

    @Override
    public void setValueToProfile() {

    }


}
