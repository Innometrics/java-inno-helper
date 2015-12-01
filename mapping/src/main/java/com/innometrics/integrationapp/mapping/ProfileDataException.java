package com.innometrics.integrationapp.mapping;

/**
 * Created by killpack on 31.08.15.
 */
public class ProfileDataException extends Throwable {
    public ProfileDataException(String valueRef) {
        super("Profile not contain data field ["+valueRef+"]");
    }
}
