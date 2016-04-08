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
        Attribute result = null;
        for (Attribute attribute : attributes) {
            if (attribute.getCollectApp().equals(tmp[0]) && attribute.getSection().equals(tmp[1])) {
                result =attribute;
            }
        }
        validateAttribute(result,tmp);
        return convertValue(result.getData().get(tmp[2]), fieldsEntry);
    }

    private void validateAttribute(Attribute attribute,String[] valueRef) throws MappingDataException {
        if (attribute==null ){
            throw new MappingDataException("Profile not contein attribute  with collectApp "+valueRef[0]+" and section "+ valueRef[1]);
        }
        if (attribute.getData()==null){
            throw new MappingDataException("Profile not contein data in attribute with collectApp "+valueRef[0]+" and section "+ valueRef[1]);
        }
        if (attribute.getData().isEmpty() || attribute.getData().containsKey(valueRef[2])){
            throw new MappingDataException("Profile not contein data wth name "+valueRef[2]+"in attribute with collectApp "+valueRef[0]+" and section "+ valueRef[1]);
        }
    }

    @Override
    public void setValueToProfile() {
        //plaseholder for set value to profile
    }
}
