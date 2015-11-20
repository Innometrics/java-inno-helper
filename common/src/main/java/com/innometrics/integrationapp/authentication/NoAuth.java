package com.innometrics.integrationapp.authentication;

import com.google.common.net.HttpHeaders;
import com.innometrics.integrationapp.constants.ProfileCloudOptions;

import java.util.Map;

/**
 * @author andrew, Innometrics
 *         If This implemetation is given
 */
public class NoAuth implements AuthMethod {
    @Override
    public void authorize(Map<ProfileCloudOptions, String> parameters, Map<String, String> headers) {
        parameters.remove(ProfileCloudOptions.app_key);
        headers.remove(HttpHeaders.AUTHORIZATION);
    }
}
