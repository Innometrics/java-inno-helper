package com.innometrics.integrationapp.authentication;

import com.google.common.net.HttpHeaders;
import com.innometrics.integrationapp.constants.ProfileCloudOptions;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author andrew, Innometrics
 */
public class AppKey implements AuthMethod {
    private final String appKey;

    public AppKey(String appKey) {
        this.appKey = appKey;
    }

    public static AppKey emptyAppKey() {
        return new AppKey(null);
    }

    @Override
    public void authorize(Map<ProfileCloudOptions, String> parameters, Map<String, String> headers) {
        headers.remove(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(appKey)) {
            parameters.put(ProfileCloudOptions.app_key, appKey);
        }
    }

}
