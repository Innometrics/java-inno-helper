package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.Attribute;
import com.innometrics.integrationapp.model.ProfileStreamMessage;

import java.util.List;


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
        //todo сделать проверку  ValueRef
        Attribute result = null;

        for (Attribute attribute : attributes) {
            if (attribute.getCollectApp().equals(tmp[0]) && attribute.getSection().equals(tmp[1])) {
                result =attribute;
            }
        }
        if (result==null ){
            throw new MappingDataException("Profile not contein attribute  with collectApp "+tmp[0]+" and section "+ tmp[1]);
        }
        if (result.getData()==null){
            throw new MappingDataException("Profile not contein data in attribute with collectApp "+tmp[0]+" and section "+ tmp[1]);
        }
        if (result.getData().isEmpty() || result.getData().containsKey(tmp[2])){
            throw new MappingDataException("Profile not contein data wth name "+tmp[2]+"in attribute with collectApp "+tmp[0]+" and section "+ tmp[1]);
        }
        return convertValue(result.getData().get(tmp[2]), fieldsEntry);
    }

    @Override
    public void setValueToProfile() {

    }


}
