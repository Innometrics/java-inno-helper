package com.innometrics.integrationapp.utils;

import com.google.gson.*;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Session;
import org.apache.commons.lang3.text.StrBuilder;

import java.lang.reflect.Type;
import java.util.Date;


public class InnoHelperUtils {
    public static final String COMPANY_ID = "INNO_COMPANY_ID";
    public static final String BUCKET_ID = "INNO_BUCKET_ID";
    public static final String APP_ID = "INNO_APP_ID";
    public static final String APP_KEY = "INNO_APP_KEY";
    public static final String API_SERVER = "INNO_API_HOST";
    public static final String API_PORT = "INNO_API_PORT";
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

    public static String getFullFirstEventName(Profile profile){
        Session session =profile.getSessions().get(0);
        StrBuilder strBuilder =new StrBuilder();
        strBuilder.append(session.getCollectApp()).append("/").append(session.getSection()).append("/");
        Event event = profile.getSessions().get(0).getEvents().get(0);
        strBuilder.append(event.getDefinitionId());
        return strBuilder.toString();
    }
}
