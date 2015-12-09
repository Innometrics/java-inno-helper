import com.google.gson.Gson;
import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.*;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import com.squareup.okhttp.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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
    public void testCreateProfile() throws IOException, ExecutionException, InterruptedException {
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
        Response response = innoHelper.saveProfile(profile);
        System.out.println(response.message());
    }

    @Test
    public void testGetProfile() throws ExecutionException, InterruptedException, IOException {
        InnoHelper innoHelper = new InnoHelper(config);
        Profile profile1 = innoHelper.getProfile("322");
        System.out.println(InnoHelperUtils.getGson().toJson(profile1));
    }

    @Test
    public void testGetManyProfile() throws ExecutionException, InterruptedException, IOException {
        InnoHelper innoHelper = new InnoHelper(config);
        long t = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
             new Thread(new Runnable() {
                @Override
                public void run() {
                    Profile profile1 = null;
                    try {
                        profile1 = innoHelper.getProfile("322");
                        System.out.println(System.currentTimeMillis()-t);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(InnoHelperUtils.getGson().toJson(profile1));
                }
            }).start();
        }
        Thread.sleep(10000);
    }

    @Test
    public void testGetDefultAppSettings() throws IOException, ExecutionException, InterruptedException {
        InnoHelper innoHelper = new InnoHelper(config);
        RulesEntry[] setting = innoHelper.getCustom("rules", RulesEntry[].class);
    }


    @Test
    public void testGetUserAppSettings() throws IOException, ExecutionException, InterruptedException {
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
