package com.innometrics.integrationapp.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.innometrics.integrationapp.model.*;
import org.apache.commons.lang3.text.StrBuilder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;


public class InnoHelperUtils {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    static final Gson gson = initGson();
    static Logger logger = Logger.getLogger(InnoHelperUtils.class.getName());
    private static final char[] chars = "1234567890abcdefghijklmnopqrstuvwxyz".toCharArray();
    // Added required cache params:
    public static final String CACHE_SIZE = "INNO_CACHE_SIZE";
    public static final String CACHE_TTL = "INNO_CACHE_TTL";

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

   public static Map<String, String> getConfigFromEnvOrDefault() {
        Map<String, String> result = new HashMap<>();
        URL url = InnoHelperUtils.class.getResource("/default.properties");
        Properties properties = new Properties();
        try {
            properties.load(url.openStream());
        } catch (IOException e) {
            logger.info("default.properties not found");
        }
        for (ConfigNames configName : ConfigNames.values()) {
            String res = System.getenv(configName.name());
            String temp = res==null ?properties.getProperty(configName.name()):res;
            if (temp!=null && !temp.isEmpty()){
                result.put(configName.name(),temp);
            }
        }
        return result;
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