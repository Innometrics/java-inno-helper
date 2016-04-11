package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.Macro;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.ProfileStreamMessage;

import java.util.Map;


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
                res = System.currentTimeMillis();
                break;
            }
            case REQUEST_IP: {
                res = profileStreamMessage.getMeta().getRequestMeta().getRequestIp();
                break;
            }
            case USER_AGENT: {
                res = profileStreamMessage.getMeta().getRequestMeta().getHeaders().get(USER_AGENT);
                break;
            }
            default: {
                throw new IllegalArgumentException("Incorrect Macro settings");
            }
        }
        return convertValue(res, fieldsEntry);
    }

    @Override
    public void setValueToProfile(Profile profile, FieldsEntry fieldsEntry, Map<String, Object> map) {
        //plaseholder for set value to profile
    }

}
