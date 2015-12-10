package com.innometrics.integrationapp;

import com.google.gson.Gson;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.mapping.DataLevel;
import com.innometrics.integrationapp.mapping.InnoTransformer;
import com.innometrics.integrationapp.mapping.ProfileDataException;
import com.innometrics.integrationapp.model.Attribute;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Session;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by killpack on 01.12.15.
 */


public class TestMapping {

    InnoTransformer getTransformer(String settingsFile) throws ExecutionException, InterruptedException, IOException {
        InnoHelper innoHelper = Mockito.mock(InnoHelper.class);
        RulesEntry[] rulesEntries = new Gson().fromJson(new FileReader(new File(getClass().getResource(settingsFile).getPath())), RulesEntry[].class);
        Mockito.when(innoHelper.getCustom(InnoTransformer.RULES, RulesEntry[].class)).thenReturn(rulesEntries);
        InnoTransformer innoTransformer = new InnoTransformer(innoHelper);
        return innoTransformer;
    }

    Profile getProfile(String settingsFile) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(new File(getClass().getResource(settingsFile).getPath())), Profile.class);
    }

    @Test
    public void testTransformFromProfile() throws ExecutionException, InterruptedException, IOException, ProfileDataException {
        InnoTransformer innoTransformer = getTransformer("/testField.json");
        Profile profile = new Profile();
        profile.setId("test");
        Session session = new Session();
        session.setCollectApp("testCollectApp");
        session.setSection("testSection");
        Event event = new Event();
        event.putData("testKey", "TestValue");
        event.setDefinitionId("EventDefinitionId");
        session.setId("sessionID");
        session.addEvent(event);
        profile.addSession(session);
        Map<String, Object> stringObjectMap = innoTransformer.fromProfile(profile);
        Assert.assertEquals("TestValue", stringObjectMap.get("test"));
    }

    @Test
    public void testTransformToProfile() throws IOException, ExecutionException, InterruptedException {
        InnoTransformer innoTransformer = getTransformer("/testFieldToProfile.json");
        Map<String, Object> data = new HashMap<>();
        data.put("test", "testValue"); // (f.e. csv header and  cell)
        Profile result = innoTransformer.toProfile(data, "test1");
        Assert.assertEquals("testValue", result.getSessions().get(0).getEvents().get(0).getData().get("test"));
    }

    @Test
    public void testDataLevelfromProfile() throws ProfileDataException, InterruptedException, ExecutionException, IOException {
        String time = RandomStringUtils.randomAlphanumeric(12);
        String url = RandomStringUtils.random(10);
        String profileId = RandomStringUtils.randomNumeric(10);
        Date createdAt = new Date();
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        data.put("time", time);

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
        fieldsEntry.setType(DataLevel.PROFILE_ID.name());
        Assert.assertEquals(profileId, transformer.getValue(profile, fieldsEntry));
        //profileCreated test
        fieldsEntry.setType(DataLevel.PROFILE_CREATED.name());
        Assert.assertEquals(createdAt.toString(), transformer.getValue(profile, fieldsEntry));
        //SESSION_CREATED test
        fieldsEntry.setType(DataLevel.SESSION_CREATED.name());
        Assert.assertEquals(createdAt.toString(), transformer.getValue(profile, fieldsEntry));

         //EVENT_CREATED  Created test
        fieldsEntry.setType(DataLevel.EVENT_CREATED.name());
        Assert.assertEquals(createdAt.toString(), transformer.getValue(profile, fieldsEntry));
        //EVENT_DEFINITION  Created test
        fieldsEntry.setType(DataLevel.EVENT_DEFINITION.name());
        Assert.assertEquals("eventDefinition", transformer.getValue(profile, fieldsEntry));



        fieldsEntry.setType(DataLevel.EVENT_DATA.name());
        fieldsEntry.setFieldName("url");
        fieldsEntry.setValueRef("url");
        Assert.assertNotNull(transformer.getValue(profile, fieldsEntry));
        Assert.assertEquals(url, transformer.getValue(profile, fieldsEntry));

        fieldsEntry.setType(DataLevel.SESSION_DATA.name());
        Assert.assertEquals(url, transformer.getValue(profile, fieldsEntry));

        fieldsEntry.setType(DataLevel.ATTRIBUTE_DATA.name());
        Attribute attribute = new Attribute();
        attribute.setData(data);
        attribute.setCollectApp("collectApp");
        attribute.setSection("section");
        profile.setAttributes(Collections.singletonList(attribute));
        fieldsEntry.setValueRef("collectApp/section/url");
        Assert.assertEquals(url, transformer.getValue(profile, fieldsEntry));

        //Todo move static and  add macros
        fieldsEntry.setType(DataLevel.STATIC.name());
        fieldsEntry.setValueRef(url);
        Assert.assertEquals(url, transformer.getValue(profile, fieldsEntry));
    }

    @Test
    public void testDataLevelToProfile() throws ProfileDataException, InterruptedException, ExecutionException, IOException {
        String time = RandomStringUtils.randomAlphanumeric(12);
        String url = RandomStringUtils.randomNumeric(10);
        String id = RandomStringUtils.randomNumeric(10);
        Map<String,Object> stringObjectMap = new HashMap<>();
        Date createdAtEvent = new Date(123123);
        Date createdAtProfile = new Date();
        Date createdAtSession = new Date();
        stringObjectMap.put("url", url);
        stringObjectMap.put("time", time);
        stringObjectMap.put("evD","eventDefinition");
        stringObjectMap.put("evCreated",createdAtEvent);
        stringObjectMap.put("profCreated",createdAtProfile);
        stringObjectMap.put("sesCreated",createdAtSession);
        stringObjectMap.put("profId",id);
        Map<String, Object> data = new HashMap<>();

        InnoTransformer transformer = getTransformer("/testDataLevelToProfile.json");
        Profile profile = transformer.toProfile(stringObjectMap,"test1");
        Assert.assertEquals(id,profile.getId());
        Assert.assertEquals(createdAtProfile,profile.getCreatedAt());
        Assert.assertEquals(createdAtSession,profile.getSessions().get(0).getCreatedAt());
        Assert.assertEquals(createdAtEvent,profile.getSessions().get(0).getEvents().get(0).getCreatedAt());
        Assert.assertEquals(url,profile.getSessions().get(0).getEvents().get(0).getData().get("event url"));
        Assert.assertEquals(url, profile.getSessions().get(0).getData().get("session url"));
        Assert.assertEquals(url, profile.getAttributes().get(0).getData().get("attribute url"));
    }

    @Test
    public void testDateConvert() throws ProfileDataException, ParseException, InterruptedException, ExecutionException, IOException {
        String time ="2015-01-01 11:11:11";
        Profile profile = new Profile();
        Event event = new Event();
        Map<String, Object> data = new HashMap<>();
        data.put("time", time);
        event.setData(data);
        Session session = new Session();
        session.setSection("section");
        session.setCollectApp("collectApp");
        session.addEvent(event);
        profile.addSession(session);
        FieldsEntry fieldsEntry =new FieldsEntry();
        fieldsEntry.setType(DataLevel.EVENT_DATA.name());
        fieldsEntry.setFieldName("url");
        Map<String, Object> fieldSettings = new HashMap<>();
        fieldSettings.put("convertType","Date");
        String timeFormat = "yyyy-MM-dd HH:mm:ss";
        fieldSettings.put("timeFormat",timeFormat);
        fieldsEntry.setFieldSettings(fieldSettings);
        fieldsEntry.setValueRef("time");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
        InnoTransformer transformer= getTransformer("/testFieldToProfile.json");
        Assert.assertNotNull(transformer.getValue(profile, fieldsEntry));
        Assert.assertEquals(simpleDateFormat.parse(time), transformer.getValue(profile, fieldsEntry));
    }
}
