package com.innometrics.integrationapp.mapping.converter;

import com.innometrics.integrationapp.appsettings.FieldsEntry;

/**
 * Created by killpack on 07.04.16.
 */
public class IntegerConverter extends InnConverter {

    @Override
    public Long convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(getAssString(value));
    }
}
