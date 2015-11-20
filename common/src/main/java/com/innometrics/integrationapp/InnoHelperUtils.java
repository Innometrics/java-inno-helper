package com.innometrics.integrationapp;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by killpack on 17.11.15.
 */
public class InnoHelperUtils {
    static final Gson gson = initGson();

    public static Gson getGson() {
        return gson;
    }

    private static Gson initGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        return builder.create();
    }
}
