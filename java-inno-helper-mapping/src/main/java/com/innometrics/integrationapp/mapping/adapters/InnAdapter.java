package com.innometrics.integrationapp.mapping.adapters;

import com.google.gson.JsonElement;
import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.ConvertType;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.mapping.util.ProfileStreamHelper;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.ProfileStreamMessage;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by killpack on 06.04.16.
 */
public abstract class InnAdapter {
    InnoHelper innoHelper;
    ProfileStreamHelper profileStreamHelper = new ProfileStreamHelper();

    public abstract Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException;

    public abstract void setValueToProfile(Profile profile, FieldsEntry fieldsEntry, Map<String, Object> map) throws MappingDataException;

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

    public Object getValue(FieldsEntry fieldsEntry, Map<String, Object> map) throws MappingDataException {
        Object o = map.get(fieldsEntry.getFieldName());
        if (o != null) {
            return convertValue(o, fieldsEntry);
        } else {
            throw new MappingDataException("profile id is required");
        }
    }

    public JsonElement getDataAsJson(Object o) {
        if (o instanceof JsonElement) {
            return (JsonElement) o;
        } else {
            return InnoHelperUtils.getGson().toJsonTree(o);
        }
    }

    public InnoHelper getInnoHelper() {
        return innoHelper;
    }

    public void setInnoHelper(InnoHelper innoHelper) {
        this.innoHelper = innoHelper;
    }
}
