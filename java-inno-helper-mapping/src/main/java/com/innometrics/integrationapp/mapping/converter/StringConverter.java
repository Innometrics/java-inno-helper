package com.innometrics.integrationapp.mapping.converter;

import com.innometrics.integrationapp.appsettings.FieldsEntry;

/**
 * Created by killpack on 07.04.16.
 */
public class StringConverter extends InnConverter {

    @Override
    public String convertValue(Object value, FieldsEntry fieldsEntry) {
        return getAssString(value);
    }
}
