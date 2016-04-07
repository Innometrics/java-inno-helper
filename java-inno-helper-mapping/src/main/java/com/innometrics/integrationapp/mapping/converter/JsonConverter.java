package com.innometrics.integrationapp.mapping.converter;

import com.google.gson.JsonElement;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.utils.InnoHelperUtils;

/**
 * Created by killpack on 07.04.16.
 */
public class JsonConverter extends InnConverter {

    @Override
    public JsonElement convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value instanceof JsonElement) {
            return (JsonElement) value;
        }
        if (value instanceof String) {
            InnoHelperUtils.getGson().fromJson((String) value, JsonElement.class);
        }
        return null;
    }
}
