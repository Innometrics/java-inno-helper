package com.innometrics.integrationapp.mapping;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.util.ProfileStreamHelper;
import com.innometrics.integrationapp.model.ProfileStreamMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by killpack on 06.04.16.
 */
public abstract class InnAdapter {
    public ProfileStreamHelper profileStreamHelper = new ProfileStreamHelper();

    abstract public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException;

    abstract public void setValueToProfile();

    public String getValueRef(FieldsEntry fieldsEntry) throws MappingDataException {
        String valueRef = fieldsEntry.getValueRef();
        if (valueRef == null || valueRef.isEmpty()) {
            throw new MappingDataException("Field valueRef in mapping must be filled ");
        }
        return valueRef;
    }


    protected Object convertValue(Object value, FieldsEntry fieldsEntry) {
        if (value == null || value.equals("null")) return null;
        DataType type = DataType.STRING;
        Map<String, Object> settings = null;
        if (fieldsEntry != null && fieldsEntry.getFieldSettings() != null) {
            try {
                settings = fieldsEntry.getFieldSettings();
                type = DataType.valueOf(String.valueOf(settings.get("convertType")).toUpperCase());
            } catch (IllegalArgumentException e) {
//                LOGGER.debug(e.getMessage());
            }
        }
        if (value instanceof JsonElement && DataType.JSON.equals(type)) {
            return value;
        }
        switch (type) {
            case JSON: {
                return value;
            }
            case DOUBLE: {
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                return Double.valueOf(getAssString(value));
            }
            case INTEGER: {
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
                return Long.valueOf(getAssString(value));
            }
            case STRING: {
                return getAssString(value);
            }
            case TIMESTAMP: {
                if (value instanceof Long) return value;
                if (value instanceof Date) return ((Date) value).getTime();
                String tmp = getAssString(value);
                if (settings.isEmpty()) return Long.valueOf(tmp);
                String format = String.valueOf(settings.get("timeFormat"));
                if (format != null && !format.isEmpty()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                    try {
                        return simpleDateFormat.parse(tmp).getTime();
                    } catch (ParseException e) {
                        return Long.valueOf(tmp);
                    }
                }
                break;
            }
            case DATE: {
                if (value instanceof Date) return value;
                if (value instanceof Long) return new Date((Long) value);
                String tmp = getAssString(value);
                if (settings.isEmpty()) return null;
                String format = String.valueOf(settings.get("timeFormat"));
                if (format != null && !format.isEmpty()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                    try {
                        return simpleDateFormat.parse(tmp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                break;
            }
        }
        return value;
    }

    String getAssString(Object o) {
        return o instanceof JsonPrimitive ? ((JsonPrimitive) o).getAsString() : String.valueOf(o);
    }
}
