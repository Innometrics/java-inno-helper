package com.innometrics.integrationapp.mapping;

import com.innometrics.integrationapp.mapping.converter.*;

/**
 * @author andrew, Innometrics
 */
public enum ConvertType {
    STRING {
        @Override
        public InnConverter getConverter() {
            return new StringConverter();
        }
    },INTEGER {
        @Override
        public InnConverter getConverter() {
            return new IntegerConverter();
        }
    },DOUBLE {
        @Override
        public InnConverter getConverter() {
            return new DoubleConverter();
        }
    }, DATE {
        @Override
        public InnConverter getConverter() {
            return new DateConverter();
        }
    }, TIMESTAMP {
        @Override
        public InnConverter getConverter() {
            return new TimeStampConverter();
        }
    }, JSON {
        @Override
        public InnConverter getConverter() {
            return new JsonConverter();
        }
    };

    public abstract  InnConverter getConverter();
}
