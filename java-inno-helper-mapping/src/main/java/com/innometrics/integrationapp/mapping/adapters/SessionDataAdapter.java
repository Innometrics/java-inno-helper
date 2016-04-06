package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.InnAdapter;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.ProfileStreamMessage;
import com.innometrics.integrationapp.model.Session;


/**
 * Created by killpack on 06.04.16.
 */
public class SessionDataAdapter extends InnAdapter {
    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        String valueRef = getValueRef(fieldsEntry);
        Session session = profileStreamHelper.getSession(profileStreamMessage);
        if (session == null || session.getData() == null || !session.getData().containsKey(valueRef)) {
            throw new MappingDataException("Profile not contain data field [SessionData/" + valueRef+"]");
        } else {
            return convertValue(session.getData().get(valueRef), fieldsEntry);
        }
    }

    @Override
    public void setValueToProfile() {

    }


}
