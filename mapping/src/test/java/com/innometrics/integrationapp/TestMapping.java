package com.innometrics.integrationapp;

import com.google.gson.Gson;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.mapping.InnoTransformer;
import com.innometrics.integrationapp.mapping.ProfileDataException;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Session;
import org.junit.Assert;
import org.junit.Test;

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
    @Test
    public void testTransformFromProfile() throws ExecutionException, InterruptedException, MalformedURLException, FileNotFoundException, ProfileDataException {
        RulesEntry[] rulesEntries = new Gson().fromJson(new FileReader(new File(getClass().getResource("/testField.json").getPath())),RulesEntry[].class);
        InnoTransformer innoTransformer = new InnoTransformer(rulesEntries);
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
        RulesEntry[] rulesEntries = new Gson().fromJson(new FileReader(new File(getClass().getResource("/testField.json").getPath())),RulesEntry[].class);
        InnoTransformer innoTransformer = new InnoTransformer(rulesEntries);
        Map<String, Object> data = new HashMap<>();
        data.put("test", "testValue"); // (f.e. csv header and  cell)
        Profile result = innoTransformer.toProfile(data);
        Assert.assertEquals("TestValue", result.getSessions().get(0).getEvents().get(0).getData().get("test"));
    }

}
