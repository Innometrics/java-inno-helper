import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.*;
import com.innometrics.integrationapp.utils.InnoHelperUtils;

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
        Map <String,String> config = new HashMap<>();
        config.put(InnoHelperUtils.API_SERVER,"http://api.innomdc.com");
        config.put(InnoHelperUtils.APP_KEY,"89oXs4UmZ325uDuA");
        config.put(InnoHelperUtils.BUCKET_ID,"bucket1");
        config.put(InnoHelperUtils.COMPANY_ID,"4");
        config.put(InnoHelperUtils.APP_ID,"sql-connector");

        InnoHelper innoHelper= new InnoHelper(config);
        RulesEntry[] rulesEntries = innoHelper.getCustom("rules", RulesEntry[].class);
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
        profile.setId("777");


        FutureTask futureTask = innoHelper.createProfile(profile);
        Profile profile1= innoHelper.getProfile("322");
        futureTask.cancel(true);
        System.out.println();
    }
}
