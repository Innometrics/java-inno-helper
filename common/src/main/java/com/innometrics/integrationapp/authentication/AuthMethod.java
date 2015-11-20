package com.innometrics.integrationapp.authentication;


import com.innometrics.integrationapp.constants.ProfileCloudOptions;

import java.io.Serializable;
import java.util.Map;

/**
 * @author andrew, Innometrics
 */
public interface AuthMethod extends Serializable {
    void authorize(Map<ProfileCloudOptions, String> parameters, Map<String, String> headers);
}
