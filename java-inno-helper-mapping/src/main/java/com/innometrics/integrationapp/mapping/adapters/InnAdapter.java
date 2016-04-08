package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.ConvertType;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.mapping.util.ProfileStreamHelper;
import com.innometrics.integrationapp.model.ProfileStreamMessage;

import java.util.Map;

/**
 * Created by killpack on 06.04.16.
 */
public abstract class InnAdapter {
    InnoHelper innoHelper;
    public ProfileStreamHelper profileStreamHelper = new ProfileStreamHelper();

    public abstract Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException;

    public abstract void setValueToProfile();

    public String getValueRef(FieldsEntry fieldsEntry) throws MappingDataException {
        String valueRef = fieldsEntry.getValueRef();
        if (valueRef == null || valueRef.isEmpty()) {
            throw new MappingDataException("Field valueRef in mapping must be filled ");
        }
        return valueRef;
    }


    protected Object convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value == null || "null".equals(value)) {
            return null;
        }
        ConvertType type = ConvertType.STRING;
        Map<String, Object> settings;
        if (fieldsEntry != null && fieldsEntry.getFieldSettings() != null) {
            settings = fieldsEntry.getFieldSettings();
            type = ConvertType.valueOf(String.valueOf(settings.get("convertType")).toUpperCase());
        }
        return type.getConverter().convertValue(value, fieldsEntry);
    }

    public InnoHelper getInnoHelper() {
        return innoHelper;
    }

    public void setInnoHelper(InnoHelper innoHelper) {
        this.innoHelper = innoHelper;
    }
}
