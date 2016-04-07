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

    @Override
    public Object getValueFromPS(ProfileStreamMessage profileStreamMessage, FieldsEntry fieldsEntry) throws MappingDataException {
        Macro macro = Macro.valueOf(getValueRef(fieldsEntry).toUpperCase());
        Object res = null;
        MetaConstant metaConstant = MetaConstant.valueOf(getValueRef(fieldsEntry).toUpperCase());
        switch (metaConstant) {
            case BUCKET_ID: {
//                return innoHelper.getBucketId();
            }
            case COMPANY_ID: {
//                return innoHelper.getCompanyId();
                //todo
            }
            case COLLECTAPP: {
                return profileStreamHelper.getSession(profileStreamMessage).getCollectApp();
            }
            case SECTION: {
                return profileStreamHelper.getSession(profileStreamMessage).getSection();
            }
        }
        return convertValue(res, fieldsEntry);
    }

    @Override
    public void setValueToProfile() {

    }

}
