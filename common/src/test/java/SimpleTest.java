import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.InnoTransformer;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.*;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
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
        profile.getSessions().add(session);
        session.getEvents().add(event);
        session.setData(data);
        Attribute attribute = new Attribute("asdasd", "asdad");
        attribute.setData(data);
        profile.getAttributes().add(attribute);
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
    public void testTransformFromProfile() throws MalformedURLException, ExecutionException, InterruptedException {
        RulesEntry[] rulesEntries = InnoHelperUtils.getGson().fromJson("",RulesEntry[].class);
        InnoTransformer innoTransformer = new InnoTransformer(rulesEntries);
        Profile profile = new Profile();
        // ....
        // get profile from the ... or create
        // ....
        Map<String, Object> result = innoTransformer.fromProfile(profile);
    }

    @Test
    public void testTransformToProfile() throws MalformedURLException, ExecutionException, InterruptedException {
        InnoHelper innoHelper = new InnoHelper(config);
        InnoTransformer innoTransformer = new InnoTransformer(innoHelper);
        // ....
        // get data  from the ... (f.e. *.csv)
        // ....
        Map<String, Object> data = new HashMap<>();
        data.put("some filed name1", "value2"); // (f.e. csv header and  cell)
        data.put("some filed name2", "value2");
        data.put("some filed name3", "value3");
        Profile result = innoTransformer.toProfile(data);
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


    private class UserSettingModel {
    }


}
