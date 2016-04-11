package com.innometrics.integrationapp.mapping.converter;

import com.innometrics.integrationapp.appsettings.FieldsEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by killpack on 07.04.16.
 */
public class TimeStampConverter extends InnConverter {

    @Override
    public Object convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value instanceof Long) {
            return  value;
        }
        if (value instanceof Date) {
            return ((Date) value).getTime();
        }
        String tmp = getAssString(value);
        if (fieldsEntry.getFieldSettings().isEmpty()) {
            return Long.valueOf(tmp);
        }
        String format = String.valueOf(fieldsEntry.getFieldSettings().get("timeFormat"));
        if (value instanceof String) {
            try {
                return parceDate((String) value, format).getTime();
            } catch (ParseException e) {
                return Long.valueOf(tmp);
            }
        }
        return null;
    }

     Date parceDate(String date, String format) throws ParseException {
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("time format mast not be empty");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(date);
    }
}
