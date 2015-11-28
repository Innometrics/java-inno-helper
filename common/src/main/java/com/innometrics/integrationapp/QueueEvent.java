package com.innometrics.integrationapp;

import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Session;

/**
 * Created by kill_pack on 28.11.2015.
 */
public class QueueEvent {
    public  String internalEventListenerKey;
    public  Profile profile;
    public  Session session;
    public  Event event;

    public QueueEvent(Profile profile) {
        this.profile = profile;
        if (profile.getSessions().size()>0){
            this.session = profile.getSessions().get(0);
            if (session.getEvents().size()>0){
                this.event = session.getEvents().get(0);
            }
        }
        this.internalEventListenerKey = session.getCollectApp() + '/' + session.getSection() + '/' + event.getDefinitionId();
    }
}
