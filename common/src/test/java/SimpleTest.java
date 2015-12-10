import com.google.gson.Gson;
import com.innometrics.integrationapp.InnoHelper;
import com.innometrics.integrationapp.appsettings.FieldSetsEntry;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.*;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

/**
 * Created by killpack on 18.11.15.
 */
public class SimpleTest {
    Map<String, String> config = new HashMap<>();
    public static final String HOST = "http://localhost";
    MockWebServer server = new MockWebServer();

    @Before
    public void init() throws IOException {
//        server.setDispatcher(new Dispatcher() {
//            @Override
//            public MockResponse dispatch(RecordedRequest recordedRequest) throws InterruptedException {
//                System.out.println(recordedRequest.getBody());
//                return new MockResponse().setBody("adadasdasdadads");
//            }
//        });
        server.start();
        config.put(InnoHelperUtils.API_SERVER, HOST);
        config.put(InnoHelperUtils.API_PORT, String.valueOf(server.getPort()));
        config.put(InnoHelperUtils.APP_KEY, "89oXs4UmZ325uDuA");
        config.put(InnoHelperUtils.BUCKET_ID, "bucket1");
        config.put(InnoHelperUtils.COMPANY_ID, "4");
        config.put(InnoHelperUtils.APP_ID, "sql-connector");
    }

    @Test
    public void testCreateProfile() throws IOException, ExecutionException, InterruptedException {
        InnoHelper innoHelper = new InnoHelper(config);
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
        server.enqueue(new MockResponse().setResponseCode(500));
        Response response = innoHelper.saveProfile(profile);
        System.out.println(response.body().string());
    }

    @Test
    public void testGetProfile() throws ExecutionException, InterruptedException, IOException {
        InnoHelper innoHelper = new InnoHelper(config);
        server.enqueue(new MockResponse().setBody("{\n" +
                "\"profile\": {\n" +
                "\"id\": \"111\",\n" +
                "\"version\": \"1.0\",\n" +
                "\"createdAt\": 1447340302127,\n" +
                "\"sessions\": [\n" +
                "{\n" +
                "\"id\": \"123\",\n" +
                "\"createdAt\": 1447340302127,\n" +
                "\"collectApp\": \"fileupload\",\n" +
                "\"section\": \"xxx\",\n" +
                "\"data\": {},\n" +
                "\"events\": [],\n" +
                "\"modifiedAt\": 1447340378848\n" +
                "}\n" +
                "],\n" +
                "\"attributes\": [],\n" +
                "\"mergedProfiles\": []\n" +
                "},\n" +
                "\"links\": {\n" +
                "\"self\": \"http://api.innomdc.com/v1/companies/222/buckets/testbucket/profiles/111\"\n" +
                "}\n" +
                "}"));
        Profile profile1 = innoHelper.getProfile("322");
        assertEquals(profile1.getId(), "111");
        assertEquals(profile1.getSessions().size(), 1);
        assertEquals(profile1.getSessions().get(0).getId(), "123");
        assertEquals(profile1.getSessions().get(0).getEvents().size(), 0);
    }

    @Test
    public void testGetManyProfile() throws ExecutionException, InterruptedException, IOException {
        InnoHelper innoHelper = new InnoHelper(config);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Profile profile1 = null;
                    try {
                        profile1 = innoHelper.getProfile("322");
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
        Thread.sleep(1000);
    }

    @Test
    public void testGetDefultAppSettings() throws IOException, ExecutionException, InterruptedException {
        server.enqueue(createResponseWithBodeyFromFile("/appSettigs.json"));
        InnoHelper innoHelper = new InnoHelper(config);
        RulesEntry[] setting = innoHelper.getCustom("rules", RulesEntry[].class);
        assertEquals(setting.length, 1);
        assertEquals(setting[0].getId(), "1430809503045");
        assertEquals(setting[0].getEvent(), "web/sfs/bobo");
        List<FieldSetsEntry> filedsets = setting[0].getFieldSets();
        assertEquals(filedsets.size(), 1);
        assertEquals(filedsets.get(0).getSetName(), "mapping");
        List<FieldsEntry> fieldsEntries = filedsets.get(0).getFields();
        assertEquals(fieldsEntries.size(), 2);
        assertEquals(fieldsEntries.get(1).getFieldName(), "id");
        assertEquals(fieldsEntries.get(1).getType(), "PROFILE_ID");

    }

    public MockResponse createResponseWithBodeyFromFile(String file) throws IOException {
        MockResponse response = new MockResponse();
        String s = IOUtils.toString(new FileInputStream(new File(getClass().getResource(file).getPath())));
        response.setBody(s);
        return response;
    }

    @Test
    public void testGetUserAppSettings() throws IOException, ExecutionException, InterruptedException {
        server.enqueue(createResponseWithBodeyFromFile("/appSettigs.json"));
        InnoHelper innoHelper = new InnoHelper(config);
        UserSettingModel setting = innoHelper.getCustom("someKey", UserSettingModel.class);
        assertEquals(setting.string, "Kurlik");
        assertTrue(setting.aFloat == 3.3f);
        assertTrue(setting.anInt == 1);
    }

    @Test
    public void Model() throws IOException, ExecutionException, InterruptedException {
        RulesEntry rulesEntry = new RulesEntry();
        String event = "event";
        String id = "id";
        String name = "name";

        rulesEntry.setEvent(event);
        assertEquals(event, rulesEntry.getEvent());
        rulesEntry.setId(id);
        assertEquals(id, rulesEntry.getId());
        rulesEntry.setName(name);
        assertEquals(name, rulesEntry.getName());

        List<FieldSetsEntry> fieldsets = new ArrayList<>();
        FieldSetsEntry setsEntry = new FieldSetsEntry();
        setsEntry.setSetName(name);
        assertEquals(name, setsEntry.getSetName());

        List<FieldsEntry> fields = new ArrayList<>();
        FieldsEntry fieldsEntry = new FieldsEntry();
        fieldsEntry.setFieldName(name);
        assertEquals(name, fieldsEntry.getFieldName());
        fieldsEntry.setRequired(true);
        assertTrue(fieldsEntry.getRequired());
        String srcType = "srcType";
        fieldsEntry.setSrcType(srcType);
        assertEquals(srcType, fieldsEntry.getSrcType());

        String value= "value";
        fieldsEntry.setValue(value);
        assertEquals(value, fieldsEntry.getValue());

        String valueRef= "valueRef";
        fieldsEntry.setValueRef(valueRef);
        assertEquals(valueRef, fieldsEntry.getValueRef());

        String type= "type";
        fieldsEntry.setType(type);
        assertEquals(type, fieldsEntry.getType());
        Map<String ,Object> map  = new HashMap<>();
        map.put("1",1);
        fieldsEntry.setFieldSettings(map);
        Assert.assertEquals(map,fieldsEntry.getFieldSettings());
        fields.add(fieldsEntry);
        setsEntry.setFields(fields);
        assertEquals(fields,setsEntry.getFields());
        fieldsets.add(setsEntry);
        rulesEntry.setFieldSets(fieldsets);
        assertEquals(fieldsets,rulesEntry.getFieldSets());
        rulesEntry.setRuleSettings(map);
        Assert.assertEquals(map,rulesEntry.getRuleSettings());

    }

    @Test
    public void testVarildation() throws MalformedURLException, ExecutionException, InterruptedException {
        Map<String, String> config = new HashMap<>();
        config.put(InnoHelperUtils.API_SERVER, "http://api.innomdc.com");
        config.put(InnoHelperUtils.APP_KEY, "89oXs4UmZ325uDuA");
        config.put(InnoHelperUtils.BUCKET_ID, "bucket1");
        config.put(InnoHelperUtils.COMPANY_ID, "4");
        try {
            InnoHelper innoHelper = new InnoHelper(config);
        } catch (IllegalArgumentException e) {
            assertEquals("In the settings missing a required field INNO_APP_ID", e.getMessage());
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
        String string;
        int anInt;
        float aFloat;
    }


}
