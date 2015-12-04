package com.innometrics.integrationapp.authentication;

import com.innometrics.integrationapp.constants.HttpHeaders;
import com.innometrics.integrationapp.constants.ProfileCloudOptions;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.util.Map;

/**
 * @author andrew, Innometrics
 */
public class BasicAuth implements AuthMethod {
    private final String encodedAuth;

    public BasicAuth(String encodedAuth) {
        this.encodedAuth = encodedAuth;
    }

    public BasicAuth(String userName, String password) {
        this(DatatypeConverter.printBase64Binary((userName + ":" + password).getBytes()));
    }

    @Override
    public void authorize(Map<ProfileCloudOptions, String> parameters, Map<String, String> headers) {
        parameters.remove(ProfileCloudOptions.app_key);
        if (!StringUtils.isEmpty(encodedAuth)) {
            headers.put(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
        }
    }
}
