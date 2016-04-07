package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.Macro;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.ProfileStreamMessage;


/**
 * Created by killpack on 06.04.16.
 */
public class MacroAdapter extends InnAdapter {
    public static final String USER_AGENT = "User-Agent";

    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        Macro macro = Macro.valueOf(getValueRef(fieldsEntry).toUpperCase());
        Object res = null;
        switch (macro) {
            case CURRENT_TIMESTAMP: {
                res =  System.currentTimeMillis();
            }
            case REQUEST_IP: {
                if (profileStreamMessage.getMeta() != null)
                   res =  profileStreamMessage.getMeta().getRequestMeta().getRequestIp();
            }
            case USER_AGENT: {
                if (profileStreamMessage.getMeta() != null)
                    return profileStreamMessage.getMeta().getRequestMeta().getHeaders().get(USER_AGENT);
            }
        }
        return convertValue(res, fieldsEntry);
    }

    @Override
    public void setValueToProfile() {

    }

}
