package com.innometrics.integrationapp.mapping;


import java.util.*;

/**
 * Created by killpack on 29.07.15.
 */
public class TransformBuilder {
    public static final String RULES = "rules";
    final Class<? extends Factory> factoryClass;
    Map<String, Factory> factories = new HashMap<>();

    public TransformBuilder(Class<? extends Factory> aClass) {
        this.factoryClass = aClass;
    }

    public Factory getFactory(String event) {
        return factories.get(event);
    }

}
