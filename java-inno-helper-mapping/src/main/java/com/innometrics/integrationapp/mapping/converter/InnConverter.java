package com.innometrics.integrationapp.mapping.converter;

import com.google.gson.JsonPrimitive;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import org.apache.log4j.Logger;


/**
 * Created by killpack on 07.04.16.
 */
public abstract  class InnConverter {
     static final Logger LOGGER = Logger.getLogger(InnConverter.class);
    public  abstract  Object convertValue(Object value, FieldsEntry fieldsEntry);
    String getAssString(Object o) {
        return o instanceof JsonPrimitive ? ((JsonPrimitive) o).getAsString() : String.valueOf(o);
    }
}
