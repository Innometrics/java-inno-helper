package com.innometrics.integrationapp.mapping.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.Attribute;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.ProfileStreamMessage;
import com.innometrics.integrationapp.utils.InnoHelperUtils;

import java.util.List;
import java.util.Map;


/**
 * Created by killpack on 06.04.16.
 */
public class AttributeDataAdapter extends InnAdapter {

    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        String[] tmp = getValueRef(fieldsEntry).split("/");
        List<Attribute> attributes = profileStreamHelper.getProfile(profileStreamMessage).getAttributes();
        if (attributes == null || attributes.isEmpty()) {
            throw new MappingDataException("Profile not Contein Attributes");
        }
        Attribute result = null;
        for (Attribute attribute : attributes) {
            if (attribute.getCollectApp().equals(tmp[0]) && attribute.getSection().equals(tmp[1])) {
                result = attribute;
            }
        }
        validateAttribute(result, tmp);
        return convertValue(result.getData().get(tmp[2]), fieldsEntry);
    }

    private void validateAttribute(Attribute attribute, String[] valueRef) throws MappingDataException {
        if (attribute == null) {
            throw new MappingDataException("Profile not contein attribute  with collectApp " + valueRef[0] + " and section " + valueRef[1]);
        }
        if (attribute.getData() == null) {
            throw new MappingDataException("Profile not contein data in attribute with collectApp " + valueRef[0] + " and section " + valueRef[1]);
        }
        if (attribute.getData().isEmpty() || attribute.getData().containsKey(valueRef[2])) {
            throw new MappingDataException("Profile not contein data wth name " + valueRef[2] + "in attribute with collectApp " + valueRef[0] + " and section " + valueRef[1]);
        }
    }

    @Override
    public void setValueToProfile(Profile profile, FieldsEntry fieldsEntry, Map<String, Object> map) throws MappingDataException {
        String[] valueRef = getValueRef(fieldsEntry).split("/");
        profile.setAttribute(valueRef[0], valueRef[1], valueRef[2], getDataAsJson(getValue(fieldsEntry, map)));
    }

}
