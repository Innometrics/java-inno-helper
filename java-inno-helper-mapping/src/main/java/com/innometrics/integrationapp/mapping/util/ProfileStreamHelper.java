package com.innometrics.integrationapp.mapping.util;

import com.google.gson.Gson;
import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.mapping.InnoTransformer;
import com.innometrics.integrationapp.mapping.MappingDataException;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.ProfileStreamMessage;
import com.innometrics.integrationapp.model.Session;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by killpack on 06.04.16.
 */
public class ProfileStreamHelper {
    public Profile getProfile(ProfileStreamMessage profileStreamMessage) throws MappingDataException {
        Profile profile = profileStreamMessage.getProfile();
        if (profile == null) {
            throw new MappingDataException("ProfileStream not contain profile");
        }
        return profile;
    }

    public  Session getSession(ProfileStreamMessage profileStreamMessage) throws MappingDataException {
        List<Session> sessions = getProfile(profileStreamMessage).getSessions();
        if (sessions == null || sessions.isEmpty()) {
            throw new MappingDataException("ProfileStream not contain session");
        }
        Session session = sessions.get(0);
        if (session == null) {
            throw new MappingDataException("ProfileStream not contain session");
        }
        return session;
    }

    public Event getEvent(ProfileStreamMessage profileStreamMessage) throws MappingDataException {
        List<Event> events = getSession(profileStreamMessage).getEvents();
        if (events == null || events.isEmpty()) {
            throw new MappingDataException("ProfileStream not contain ecent");
        }
        Event event = events.get(0);
        if (event == null) {
            throw new MappingDataException("ProfileStream not contain evrnt");
        }
        return event;
    }

    public static InnoHelper createMockHelper(String settingsPath){
        InnoHelper innoHelper = Mockito.mock(InnoHelper.class);
        try {
            RulesEntry[] rulesEntries = new Gson().fromJson(new FileReader(new File(settingsPath)), RulesEntry[].class);
            Mockito.when(innoHelper.getCustom(InnoTransformer.RULES, RulesEntry[].class)).thenReturn(rulesEntries);
            Mockito.when(innoHelper.getRulesEntries()).thenReturn(rulesEntries);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return innoHelper;
    }
}
