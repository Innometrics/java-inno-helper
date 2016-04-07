package com.innometrics.integrationapp.mapping.converter;

import com.innometrics.integrationapp.appsettings.FieldsEntry;

/**
 * Created by killpack on 07.04.16.
 */
public class DoubleConverter extends InnConverter {

    @Override
    public Double convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.valueOf(getAssString(value));
    }
}
