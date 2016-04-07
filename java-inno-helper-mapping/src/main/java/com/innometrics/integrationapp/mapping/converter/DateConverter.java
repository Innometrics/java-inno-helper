package com.innometrics.integrationapp.mapping.converter;

import com.innometrics.integrationapp.appsettings.FieldsEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by killpack on 07.04.16.
 */
public class DateConverter extends InnConverter {

    @Override
    public Date convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value instanceof Date) return (Date) value;
        if (value instanceof Long) return new Date((Long) value);
        String tmp = getAssString(value);
        String format = String.valueOf(fieldsEntry.getFieldSettings().get("timeFormat"));
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("");//todo set Exception massage
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(tmp);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
