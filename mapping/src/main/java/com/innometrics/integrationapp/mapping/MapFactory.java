package com.innometrics.integrationapp.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by killpack on 30.07.15.
 */
public class MapFactory extends Factory<Map<String, Object>> {
    @Override
    protected Map<String, Object> processField(Map<String, Object> previousResult, String key, Object src) {
        if (previousResult == null) {
            previousResult = new HashMap<>();
        }
        previousResult.put(key, src);
        return previousResult;
    }
}
