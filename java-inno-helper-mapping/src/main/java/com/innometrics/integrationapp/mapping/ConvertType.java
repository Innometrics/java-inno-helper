package com.innometrics.integrationapp.mapping;

import com.innometrics.integrationapp.mapping.converter.*;

/**
 * @author andrew, Innometrics
 */
public enum ConvertType {
    STRING {
        @Override
        InnConverter getConverter() {
            return new StringConverter();
        }
    },INTEGER {
        @Override
        InnConverter getConverter() {
            return new IntegerConverter();
        }
    },DOUBLE {
        @Override
        InnConverter getConverter() {
            return new DoubleConverter();
        }
    }, DATE {
        @Override
        InnConverter getConverter() {
            return new DateConverter();
        }
    }, TIMESTAMP {
        @Override
        InnConverter getConverter() {
            return new TimeStampConverter();
        }
    }, JSON {
        @Override
        InnConverter getConverter() {
            return new JsonConverter();
        }
    };

    abstract InnConverter getConverter();
}
