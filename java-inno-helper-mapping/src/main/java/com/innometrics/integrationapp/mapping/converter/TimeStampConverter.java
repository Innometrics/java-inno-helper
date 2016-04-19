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
        Object result = null;
        String tmp = getAssString(value);
        if (value instanceof Long) {
            result= value;
        }else if (value instanceof Date) {
            result=((Date) value).getTime();
        }else if (fieldsEntry.getFieldSettings().isEmpty()) {
            result= Long.valueOf(tmp);
        }else if (value instanceof String) {
            String format = String.valueOf(fieldsEntry.getFieldSettings().get("timeFormat"));
            try {
                result= parceDate((String) value, format).getTime();
            } catch (ParseException e) {
                result= Long.valueOf((String)value);
            }
        }
        return result;
    }

     Date parceDate(String date, String format) throws ParseException {
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("time format mast not be empty");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(date);
    }
}
