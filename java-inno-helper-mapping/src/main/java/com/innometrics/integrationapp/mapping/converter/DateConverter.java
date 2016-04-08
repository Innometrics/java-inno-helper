package com.innometrics.integrationapp.mapping.converter;

import com.innometrics.integrationapp.appsettings.FieldsEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by killpack on 07.04.16.
 */
public class DateConverter extends TimeStampConverter {

    @Override
    public Object convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value instanceof Date) {
            return value;
        }
        if (value instanceof Long) {
            return new Date((Long) value);
        }
        String tmp = getAssString(value);
        String format = String.valueOf(fieldsEntry.getFieldSettings().get("timeFormat"));
        try {
            return parceDate(tmp, format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
