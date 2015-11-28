package com.innometrics.integrationapp.mapping;


import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Session;
import org.apache.commons.lang3.text.StrBuilder;

/**
 * Created by killpack on 29.07.15.
 */
public class Utils  {
    public static String getFullEventName(Profile profile){
        Session session =profile.getSessions().get(0);
        StrBuilder strBuilder =new StrBuilder();
        strBuilder.append(session.getCollectApp()).append("/").append(session.getSection()).append("/");
        Event event = profile.getSessions().get(0).getEvents().get(0);
        strBuilder.append(event.getDefinitionId());
        return strBuilder.toString();
    }
}
