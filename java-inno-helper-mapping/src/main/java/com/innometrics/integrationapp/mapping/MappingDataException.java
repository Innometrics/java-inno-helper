package com.innometrics.integrationapp.mapping;

/**
 * Created by killpack on 31.08.15.
 */
public class MappingDataException extends Exception {
    public MappingDataException(String valueRef) {
        super(valueRef);
    }
}
