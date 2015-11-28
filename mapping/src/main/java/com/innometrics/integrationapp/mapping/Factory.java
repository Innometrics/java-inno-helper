package com.innometrics.integrationapp.mapping;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.innometrics.integrationapp.appsettings.FieldSetsEntry;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.Attribute;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


/**
 * Created by killpack on 29.07.15.
 */
public abstract class Factory<Result> {
    RulesEntry rulesEntry;
    public static final String MAPPING_SET_NAME = "mapping";
    List<FieldsEntry> fields;

    public void setRulesEntry(RulesEntry rulesEntry) {
        this.rulesEntry = rulesEntry;
        for (FieldSetsEntry fieldSetsEntry : rulesEntry.getFieldSets()) {
            if (MAPPING_SET_NAME.equals(fieldSetsEntry.getSetName())) {
                fields = fieldSetsEntry.getFields();
            }
        }
        init();
    }

    public void init() {
    }

    protected abstract Result processField(Result previousResult, String key, Object src);

    public Result getResult(Profile profile) {
        Result result = null;
        if (fields != null && !fields.isEmpty()) {
            for (FieldsEntry field : fields) {
                result = processField(result, field.getFieldName(), getValue(profile,field));
            }
        }
        return result;
    }

    public Object getValue(Profile profile, FieldsEntry fieldsEntry) {
        String result = null;
        String stringType = fieldsEntry.getType();
        if (stringType==null || stringType.isEmpty())return null;
        DataLevel type = DataLevel.valueOf(stringType.toUpperCase());
        Session session = profile.getSessions().get(0);
        String[] valueRef = fieldsEntry.getValueRef().split("/");
        switch (type) {
            case EVENTDATA: {
                    result = (String) session.getEvents().get(0).getData().get(valueRef[2]);
                break;
            }
            case SESIONDATA: {
                    result = (String) session.getData().get(valueRef[2]);
                break;
            }
            case ATTRIBUTEDATA: {
                for (Attribute attribute : profile.getAttributes()) {
                    if (attribute.getCollectApp().equals(valueRef[0]) && attribute.getSection().equals(valueRef[1])) {
                        result = (String) attribute.getData().get(valueRef[2]);
                    }
                }
                break;
            }
            case STATIC: {
                result = fieldsEntry.getValueRef();
                break;
            }
            case PROFILEID: {
                result = profile.getId();
                break;
            }
            case PROFILECRETED: {
                result = profile.getCreatedAt().toString();
                break;
            }
            case SESSIONCREATED: {
                result = session.getCreatedAt().toString();
                break;
            }
            case EVENTCREATED: {
                result = session.getEvents().get(0).getCreatedAt().toString();
                break;
            }case EVENTDEFINITION: {
                result = session.getEvents().get(0).getDefinitionId();
                break;
            }
            case MACROS: {
//                result = fieldsEntry.a();// todo implementMacros
                break;
            }
        }
        return convertValue(result, fieldsEntry);
    }

    protected static Object convertValue(String rawValue, FieldsEntry fieldsEntry) {
        if (rawValue == null || rawValue.equals("null")) return null;
        DataType type = DataType.STRING;
        Map<String, Object> settings = null;
        if (fieldsEntry != null && fieldsEntry.getFieldSettings() != null) {
            try {
                settings = fieldsEntry.getFieldSettings();
                type = DataType.valueOf(String.valueOf(settings.get("convertType")).toUpperCase());
            } catch (IllegalArgumentException e) {
//                logger.debug(e.getMessage());
            }
        }
        switch (type) {
            case DOUBLE: {
                return Double.valueOf(rawValue);
            }
            case INTEGER: {
                return Integer.valueOf(rawValue);
            }
            case TIMESTAMP: {
                if (settings.isEmpty()) return Long.valueOf(rawValue);
                String format = (String) settings.get("timeFormat");
                if (format != null && !format.isEmpty()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                    try {
                        return simpleDateFormat.parse(rawValue).getTime();
                    } catch (ParseException e) {
                        return Long.valueOf(rawValue);
                    }
                }
                break;
            }
            case LONG: {
                return Long.valueOf(rawValue);
            }
            case STRING: {
                return rawValue;
            }
            case DATE: {
                if (settings.isEmpty()) return null;
                String format = (String) settings.get("timeFormat");
                if (format != null && !format.isEmpty()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                    try {
                        return simpleDateFormat.parse(rawValue);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                break;
            }
        }
        return rawValue;
    }
}
