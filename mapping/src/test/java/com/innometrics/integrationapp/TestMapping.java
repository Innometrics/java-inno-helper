package com.innometrics.integrationapp;

import com.google.gson.Gson;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.mapping.InnoTransformer;
import com.innometrics.integrationapp.mapping.ProfileDataException;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Session;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by killpack on 01.12.15.
 */


public class TestMapping {
    Map<String, String> config = new HashMap<>();

    @Before
    public void init() {
        config.put(InnoHelperUtils.API_SERVER, "http://api.innomdc.com");
        config.put(InnoHelperUtils.APP_KEY, "89oXs4UmZ325uDuA");
        config.put(InnoHelperUtils.BUCKET_ID, "bucket1");
        config.put(InnoHelperUtils.COMPANY_ID, "4");
        config.put(InnoHelperUtils.APP_ID, "sql-connector");
    }

    InnoTransformer getTransformer(String settingsFile) throws ExecutionException, InterruptedException, FileNotFoundException {
        InnoHelper innoHelper = Mockito.mock(InnoHelper.class);
        RulesEntry[] rulesEntries = new Gson().fromJson(new FileReader(new File(getClass().getResource(settingsFile).getPath())), RulesEntry[].class);
        Mockito.when(innoHelper.getCustom(InnoTransformer.RULES, RulesEntry[].class)).thenReturn(rulesEntries);
        InnoTransformer innoTransformer = new InnoTransformer(innoHelper);
        return innoTransformer;
    }

    @Test
    public void testTransformFromProfile() throws ExecutionException, InterruptedException, MalformedURLException, FileNotFoundException, ProfileDataException {
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
    public void testTransformToProfile() throws MalformedURLException, ExecutionException, InterruptedException, FileNotFoundException {
        InnoTransformer innoTransformer = getTransformer("/testFieldToProfile.json");
        Map<String, Object> data = new HashMap<>();
        data.put("test", "testValue"); // (f.e. csv header and  cell)
        Profile result = innoTransformer.toProfile(data, "test1");
        Assert.assertEquals("testValue", result.getSessions().get(0).getEvents().get(0).getData().get("test"));
    }

}
