package com.innometrics.integrationapp.mapping.adapters;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.Macro;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.mapping.MetaConstant;
import com.innometrics.integrationapp.model.ProfileStreamMessage;


/**
 * Created by killpack on 06.04.16.
 */
public class MetaAdapter extends InnAdapter {

    public static final String INCORRECT_META_SETTINGS = "Incorrect Meta settings";

    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        Object res ;
        MetaConstant metaConstant = MetaConstant.valueOf(getValueRef(fieldsEntry).toUpperCase());
        switch (metaConstant) {
            case BUCKET_ID: {
                res = innoHelper.getBucketId();
                break;
            }
            case COMPANY_ID: {
                res = innoHelper.getCompanyId();
                break;
            }
            case COLLECTAPP: {
                res = profileStreamHelper.getSession(profileStreamMessage).getCollectApp();
                break;
            }
            case SECTION: {
                return profileStreamHelper.getSession(profileStreamMessage).getSection();
            }
            default: {
                throw new IllegalArgumentException(INCORRECT_META_SETTINGS);
            }
        }
        return convertValue(res, fieldsEntry);
    }

    @Override
    public void setValueToProfile() {
        //plaseholder for set value to profile

    }

}
