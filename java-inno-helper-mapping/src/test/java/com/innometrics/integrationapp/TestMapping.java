package com.innometrics.integrationapp;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.mapping.DataLevel;
import com.innometrics.integrationapp.mapping.InnoTransformer;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.*;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.innometrics.integrationapp.mapping.DataLevel.*;
/**
 * Created by killpack on 01.12.15.
 */


public class TestMapping {

    InnoTransformer getTransformer(String settingsFile) throws Exception {
        InnoHelper innoHelper = Mockito.mock(InnoHelper.class);
        RulesEntry[] rulesEntries = new Gson().fromJson(new FileReader(new File(getClass().getResource(settingsFile).getPath())), RulesEntry[].class);
        Mockito.when(innoHelper.getCustom(InnoTransformer.RULES, RulesEntry[].class)).thenReturn(rulesEntries);
        InnoTransformer innoTransformer = new InnoTransformer(innoHelper);
        return innoTransformer;
    }

    Profile getProfile(String settingsFile) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(new File(getClass().getResource(settingsFile).getPath())), Profile.class);
    }

//    @Test
//    public void testTransformFromProfile() throws Exception, MappingDataException {
//        InnoTransformer innoTransformer = getTransformer("/testField.json");
//        Profile profile = new Profile();
//        profile.setId("test");
//        Session session = new Session();
//        session.setCollectApp("testCollectApp");
//        session.setSection("testSection");
//        Event event = new Event();
//        event.putData("testKey", new JsonPrimitive("TestValue"));
//        event.setDefinitionId("EventDefinitionId");
//        session.setId("sessionID");
//        session.addEvent(event);
//        profile.addSession(session);
//
//        Map<String, Object> stringObjectMap = innoTransformer.fromProfileStream(new ProfileStreamMessage(profile));
//        Assert.assertEquals("TestValue", stringObjectMap.get("test"));
//    }

    @Test
    public void testTransformFromProfileMultiThread() throws Exception, MappingDataException {
        final InnoTransformer innoTransformer = getTransformer("/testField.json");
        final Profile profile = new Profile();
        profile.setId("test");
        Session session = new Session();
        session.setCollectApp("testCollectApp");
        session.setSection("testSection");
        Event event = new Event();
        event.putData("testKey", new JsonPrimitive("TestValue"));
        event.setDefinitionId("EventDefinitionId");
        session.setId("sessionID");
        session.addEvent(event);
        profile.addSession(session);
        final ProfileStreamMessage startProfile = InnoHelperUtils.getGson().fromJson(new FileReader(new File(getClass().getResource("/profileStreamMessage.json").getPath())), ProfileStreamMessage.class);
        startProfile.setProfile(profile);
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, Object> stringObjectMap = innoTransformer.fromProfileStream(startProfile);
                        Assert.assertEquals("TestValue", stringObjectMap.get("test"));
                    } catch (MappingDataException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(1000);
    }

    @Test
    public void testTransformToProfile() throws Exception {
        InnoTransformer innoTransformer = getTransformer("/testFieldToProfile.json");
        Map<String, Object> data = new HashMap<>();
        data.put("test", "testValue"); // (f.e. csv header and  cell)
        Profile result = innoTransformer.toProfile(data, "test1");
        Assert.assertEquals(new JsonPrimitive("testValue"), result.getSessions().get(0).getEvents().get(0).getData().get("test"));
    }

    @Test
    public void testDataLevelfromProfile() throws MappingDataException, Exception {
        String time = RandomStringUtils.randomAlphanumeric(12);
        String url = RandomStringUtils.random(10);
        String profileId = RandomStringUtils.randomNumeric(10);
        Date createdAt = new Date();
        Map<String, JsonElement> data = new HashMap<>();
        data.put("url", new JsonPrimitive(url));
        data.put("time", new JsonPrimitive(time));

        Profile profile = new Profile(profileId);
        profile.setCreatedAt(createdAt);
        Event event = new Event();
        event.setData(data);
        event.setDefinitionId("eventDefinition");
        event.setCreatedAt(createdAt);
        Session session = new Session();
        session.setCreatedAt(createdAt);
        session.setData(data);
        session.addEvent(event);

        profile.addSession(session);
        profile.getSessions().get(0).setCollectApp("collectApp");
        InnoTransformer transformer = getTransformer("/testFieldToProfile.json");

        FieldsEntry fieldsEntry = new FieldsEntry();
        //profile id test
        fieldsEntry.setType(PROFILE_ID.name());
        Assert.assertEquals(profileId, PROFILE_ID.getAdapter().getValueFromPS(new ProfileStreamMessage(profile), fieldsEntry));
        //profileCreated test
        fieldsEntry.setType(DataLevel.PROFILE_CREATED.name());
        Assert.assertEquals(createdAt.toString(), PROFILE_CREATED.getAdapter().getValueFromPS(new ProfileStreamMessage(profile), fieldsEntry));
        //SESSION_CREATED test
        fieldsEntry.setType(DataLevel.SESSION_CREATED.name());
        Assert.assertEquals(createdAt.toString(), SESSION_CREATED.getAdapter().getValueFromPS(new ProfileStreamMessage(profile), fieldsEntry));

        //EVENT_CREATED  Created test
        fieldsEntry.setType(DataLevel.EVENT_CREATED.name());
        Assert.assertEquals(createdAt.toString(), EVENT_CREATED.getAdapter().getValueFromPS(new ProfileStreamMessage(profile), fieldsEntry));
        //EVENT_DEFINITION  Created test
        fieldsEntry.setType(DataLevel.EVENT_DEFINITION.name());
        Assert.assertEquals("eventDefinition", EVENT_DEFINITION.getAdapter().getValueFromPS(new ProfileStreamMessage(profile), fieldsEntry));
//

        fieldsEntry.setType(DataLevel.EVENT_DATA.name());
        fieldsEntry.setFieldName("url");
        fieldsEntry.setValueRef("url");
        Assert.assertNotNull(EVENT_DATA.getAdapter().getValueFromPS(new ProfileStreamMessage(profile), fieldsEntry));
        Assert.assertEquals(new JsonPrimitive(url).getAsString(), EVENT_DATA.getAdapter().getValueFromPS(new ProfileStreamMessage(profile), fieldsEntry));
//
        fieldsEntry.setType(DataLevel.SESSION_DATA.name());
        Assert.assertEquals(url, SESSION_DATA.getAdapter().getValueFromPS(new ProfileStreamMessage(profile), fieldsEntry));

        fieldsEntry.setType(DataLevel.ATTRIBUTE_DATA.name());
        Attribute attribute = new Attribute();
        attribute.setData(data);
        attribute.setCollectApp("collectApp");
        attribute.setSection("section");
        profile.setAttributes(Collections.singletonList(attribute));
        fieldsEntry.setValueRef("collectApp/section/url");
        Assert.assertEquals(url, ATTRIBUTE_DATA.getAdapter().getValueFromPS(new ProfileStreamMessage(profile), fieldsEntry));

        //Todo move static and  add macros
        fieldsEntry.setType(DataLevel.STATIC.name());
        fieldsEntry.setValueRef(url);
        Assert.assertEquals(url, STATIC.getAdapter().getValueFromPS(new ProfileStreamMessage(profile), fieldsEntry));
    }

    @Test
    public void testDataLevelToProfile() throws MappingDataException, Exception {
        String time = RandomStringUtils.randomAlphanumeric(12);
        String url = RandomStringUtils.randomNumeric(10);
        String id = RandomStringUtils.randomNumeric(10);
        Map<String, Object> stringObjectMap = new HashMap<>();
        Date createdAtEvent = new Date(123123);
        Date createdAtProfile = new Date();
        Date createdAtSession = new Date();
        stringObjectMap.put("url", url);
        stringObjectMap.put("time", time);
        stringObjectMap.put("evD", "eventDefinition");
        stringObjectMap.put("evCreated", createdAtEvent);
        stringObjectMap.put("profCreated", createdAtProfile);
        stringObjectMap.put("sesCreated", createdAtSession);
        stringObjectMap.put("profId", id);

        InnoTransformer transformer = getTransformer("/testDataLevelToProfile.json");
        Profile profile = transformer.toProfile(stringObjectMap, "test1");
        Assert.assertEquals(id, profile.getId());
        Assert.assertEquals(createdAtProfile, profile.getCreatedAt());
        Assert.assertEquals(createdAtSession, profile.getSessions().get(0).getCreatedAt());
        Assert.assertEquals(createdAtEvent, profile.getSessions().get(0).getEvents().get(0).getCreatedAt());
        Assert.assertEquals(new JsonPrimitive(url), profile.getSessions().get(0).getEvents().get(0).getData().get("event url"));
        Assert.assertEquals(new JsonPrimitive(url), profile.getSessions().get(0).getData().get("session url"));
        Assert.assertEquals(new JsonPrimitive(url), profile.getAttributes().get(0).getData().get("attribute url"));
    }

    @Test
    public void testDateConvert() throws MappingDataException, Exception {
        String time = "2015-01-01 11:11:11";
        Profile profile = new Profile();
        Event event = new Event();
        Map<String, JsonElement> data = new HashMap<>();
        data.put("time", new JsonPrimitive(time));
        event.setData(data);
        Session session = new Session();
        session.setSection("section");
        session.setCollectApp("collectApp");
        session.addEvent(event);
        profile.addSession(session);
        FieldsEntry fieldsEntry = new FieldsEntry();
        fieldsEntry.setType(DataLevel.EVENT_DATA.name());
        fieldsEntry.setFieldName("url");
        Map<String, Object> fieldSettings = new HashMap<>();
        fieldSettings.put("convertType", "Date");
        String timeFormat = "yyyy-MM-dd HH:mm:ss";
        fieldSettings.put("timeFormat", timeFormat);
        fieldsEntry.setFieldSettings(fieldSettings);
        fieldsEntry.setValueRef("time");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
        InnoTransformer transformer = getTransformer("/testFieldToProfile.json");
//        Assert.assertNotNull(transformer.getValue(new ProfileStreamMessage(profile), fieldsEntry));
//        Assert.assertEquals(simpleDateFormat.parse(time), transformer.getValue(new ProfileStreamMessage(profile), fieldsEntry));
    }



    @Test
    public void testMetaAndMacro() throws Exception, MappingDataException {
        InnoTransformer innoTransformer = getTransformer("/testField.json");
        ProfileStreamMessage startProfile = InnoHelperUtils.getGson().fromJson(new FileReader(new File(getClass().getResource("/profileStreamMessage.json").getPath())), ProfileStreamMessage.class);
        Map<String, Object> stringObjectMap = innoTransformer.fromProfileStream(startProfile);
        Assert.assertEquals("testCollectApp", stringObjectMap.get("test2"));
        Assert.assertEquals("testSection", stringObjectMap.get("test3"));
        Assert.assertEquals(null, stringObjectMap.get("test4"));
        Assert.assertEquals("188.112.192.214", stringObjectMap.get("test5"));
        Assert.assertEquals("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36", stringObjectMap.get("test6"));
        Assert.assertTrue(System.currentTimeMillis() >= Long.valueOf((String) stringObjectMap.get("test7")));
    }
}
