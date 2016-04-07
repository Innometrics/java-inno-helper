package com.innometrics.integrationapp.mapping;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.util.ProfileStreamHelper;
import com.innometrics.integrationapp.model.ProfileStreamMessage;

import java.util.Map;

/**
 * Created by killpack on 06.04.16.
 */
public abstract class InnAdapter {
    public ProfileStreamHelper profileStreamHelper = new ProfileStreamHelper();

    abstract public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException;

    abstract public void setValueToProfile();

    public String getValueRef(FieldsEntry fieldsEntry) throws MappingDataException {
        String valueRef = fieldsEntry.getValueRef();
        if (valueRef == null || valueRef.isEmpty()) {
            throw new MappingDataException("Field valueRef in mapping must be filled ");
        }
        return valueRef;
    }


    protected Object convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value == null || value.equals("null")) return null;
        ConvertType type = ConvertType.STRING;
        Map<String, Object> settings = null;
        if (fieldsEntry != null && fieldsEntry.getFieldSettings() != null) {
            settings = fieldsEntry.getFieldSettings();
            type = ConvertType.valueOf(String.valueOf(settings.get("convertType")).toUpperCase());
        }
        return type.getConverter().convertValue(value, fieldsEntry);
    }


}
