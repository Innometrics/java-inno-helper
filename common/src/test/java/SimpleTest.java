import com.google.gson.Gson;
import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.*;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by killpack on 18.11.15.
 */
public class SimpleTest {
    Map<String, String> config = new HashMap<>();

    @Before
    public void init() {
        config.put(InnoHelperUtils.API_SERVER, "http://api.innomdc.com");
        config.put(InnoHelperUtils.APP_KEY, "89oXs4UmZ325uDuA");
        config.put(InnoHelperUtils.BUCKET_ID, "bucket1");
        config.put(InnoHelperUtils.COMPANY_ID, "4");
        config.put(InnoHelperUtils.APP_ID, "sql-connector");
    }

    @Test
    public void testCreateProfile() throws MalformedURLException, ExecutionException, InterruptedException {
        InnoHelper innoHelper = new InnoHelper(config);
        // create profile
        Profile profile = new Profile();
        Session session = new Session();
        Event event = new Event();
        Map<String, Object> data = new HashMap<>();
        data.put("asdad", 13);
        session.setId("section");
        event.setId("123123123123");
        event.setDefinitionId("");
        session.setSection("sdadad");
        event.setData(data);
        profile.addSession(session);
        session.addEvent(event);
        session.setData(data);
        Attribute attribute = new Attribute("asdasd", "asdad");
        attribute.setData(data);
        profile.addAttribute(attribute);
        profile.setId("002");
        // end of create profile


        FutureTask futureTask = innoHelper.saveProfile(profile);

        futureTask.get();
//        innoHelper.mergeProfile("some temp profile id ", "some temp profile id2 ", "some temp profile id2 ");
//        innoHelper.evaluateProfile(profile);// todo  IQL segments
    }

    @Test
    public void testGetProfile() throws ExecutionException, InterruptedException, MalformedURLException {
        InnoHelper innoHelper = new InnoHelper(config);
        Profile profile1 = innoHelper.getProfile("322").get().getRight();
        System.out.println(InnoHelperUtils.getGson().toJson(profile1));
    }


    @Test
    public void testGetDefultAppSettings() throws MalformedURLException, ExecutionException, InterruptedException {
        InnoHelper innoHelper = new InnoHelper(config);
        RulesEntry[] setting = innoHelper.getCustom("rules", RulesEntry[].class);
    }


    @Test
    public void testGetUserAppSettings() throws MalformedURLException, ExecutionException, InterruptedException {
        InnoHelper innoHelper = new InnoHelper(config);
        UserSettingModel setting = innoHelper.getCustom("some  key", UserSettingModel.class);
    }

    @Test
    public void testVarildation() throws MalformedURLException, ExecutionException, InterruptedException {
        Map <String,String>config = new HashMap<>();
        config.put(InnoHelperUtils.API_SERVER, "http://api.innomdc.com");
        config.put(InnoHelperUtils.APP_KEY, "89oXs4UmZ325uDuA");
        config.put(InnoHelperUtils.BUCKET_ID, "bucket1");
        config.put(InnoHelperUtils.COMPANY_ID, "4");
        try {
            InnoHelper innoHelper = new InnoHelper(config);
        }catch (IllegalArgumentException e){
            Assert.assertEquals("In the settings missing a required field INNO_APP_ID", e.getMessage());
        }
    }

    @Test
    public void testDirtyProfile() throws MalformedURLException, ExecutionException, InterruptedException {
        InnoHelper innoHelper = new InnoHelper(config);
        Profile profile = new Profile();
        Session session = new Session();
        session.setId("`123123");
        Event event = new Event();
        event.setDefinitionId("1234");
        event.setId("ad");
        session.addEvent(event);
        profile.addSession(session);
        profile.resetDirty();
//        Event event2 = new Event();
//        event2.setId("q34");
        event.putData("adadasdas", 121233);
//        event2.setDefinitionId("asaadaaasdaasdsad");
//        session.addEvent(event2);
//        session.setDirty(true);
        System.out.println(InnoHelperUtils.getGson().toJson(profile));
        System.out.println(new Gson().toJson(profile));
    }


    private class UserSettingModel {
    }


}
