package com.innometrics.integrationapp.mapping.converter;

import com.innometrics.integrationapp.appsettings.FieldsEntry;

/**
 * Created by killpack on 07.04.16.
 */
public class IntegerConverter extends InnConverter {

    @Override
    public Integer convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.valueOf(getAssString(value));
    }
}
