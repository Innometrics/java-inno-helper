package com.innometrics.integrationapp.model;

import com.google.gson.JsonElement;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.utils.InnoHelperUtils;


/**
 * Created by killpack on 24.11.15.
 */
public class DefaultAppSettings   {
    App app;

    public DefaultAppSettings(App app) {
        this.app = app;
    }

    public RulesEntry[] getRules(){
        return InnoHelperUtils.getGson().fromJson((JsonElement)app.getCustom().get("rules"),RulesEntry[].class);
    }
}
