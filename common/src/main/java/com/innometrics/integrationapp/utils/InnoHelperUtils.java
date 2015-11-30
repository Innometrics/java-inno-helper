package com.innometrics.integrationapp.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.innometrics.integrationapp.model.*;
import org.apache.commons.lang3.text.StrBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class InnoHelperUtils {
    public static final String COMPANY_ID = "INNO_COMPANY_ID";
    public static final String BUCKET_ID = "INNO_BUCKET_ID";
    public static final String APP_ID = "INNO_APP_ID";
    public static final String APP_KEY = "INNO_APP_KEY";
    public static final String API_SERVER = "INNO_API_HOST";
    public static final String API_PORT = "INNO_API_PORT";
    static final Gson gson = initGson();
    private static final char[] chars = "1234567890abcdefghijklmnopqrstuvwxyz".toCharArray();

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
        Type collectionTypeSession = new TypeToken<List<Session>>() {
        }.getType();
        Type collectionTypeEvent = new TypeToken<List<Event>>() {
        }.getType();
        Type collectionTypeAttribute = new TypeToken<List<Attribute>>() {
        }.getType();
        builder.registerTypeAdapter(collectionTypeSession, new DirtySerializer<Session>());
        builder.registerTypeAdapter(collectionTypeEvent, new DirtySerializer<Event>());
        builder.registerTypeAdapter(collectionTypeAttribute, new DirtySerializer<Attribute>());
        return builder.create();
    }

    public static String getFullFirstEventName(Profile profile) {
        Session session = profile.getSessions().get(0);
        StrBuilder strBuilder = new StrBuilder();
        strBuilder.append(session.getCollectApp()).append("/").append(session.getSection()).append("/");
        Event event = profile.getSessions().get(0).getEvents().get(0);
        strBuilder.append(event.getDefinitionId());
        return strBuilder.toString();
    }

    private static Random r = new Random();

    public static String getRandomID(int length) {
        String newUUID = "";
        for (int i = 0; i < length; i++) {
            newUUID += chars[r.nextInt(35)];
        }
        return newUUID;
    }
}

class DirtySerializer<T extends Dirty> implements JsonSerializer<List<T>> {
    @Override
    public JsonElement serialize(List<T> ts, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray jsonElements = new JsonArray();
        for (T dirty : ts) {
            if (dirty.isDirty()) {
                jsonElements.add(jsonSerializationContext.serialize(dirty, ((ParameterizedType) type).getActualTypeArguments()[0]));
            }
        }
        return jsonElements;
    }


}