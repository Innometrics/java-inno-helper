package com.innometrics.integrationapp;

import com.innometrics.integrationapp.authentication.AppKey;
import com.innometrics.integrationapp.model.*;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by killpack on 18.11.15.
 */
public class Test {
    public static void main(String[] args) throws MalformedURLException, ExecutionException, InterruptedException {
        ProfileCloud profileCloud = new ProfileCloud("http://api.innomdc.com");
        profileCloud.withAuth(new AppKey("89oXs4UmZ325uDuA"));
        App app = profileCloud.getApp("4", "bucket1", "sql-connector");


        Profile profile=  new Profile();
        Session session = new Session();
        Event event =new Event();
        Map<String ,Object > data= new HashMap<>();
        data.put("asdad",13);

        session.setId("section");
        event.setId("123123123123");
        event.setDefinitionId("");
        session.setSection("sdadad");

        event.setData(data);
        profile.getSessions().add(session);
        session.getEvents().add(event);
        session.setData(data);
        Attribute attribute = new Attribute("asdasd","asdad");
        attribute.setData(data);
        profile.getAttributes().add(attribute);
        profile.setId("322");


        FutureTask futureTask = profileCloud.createProfile("4", "bucket1", profile);
        Profile profile1= profileCloud.getProfile("4", "bucket1", "322");
        System.out.println();
    }
}
