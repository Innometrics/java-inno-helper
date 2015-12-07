package com.innometrics.integrationapp;

import com.google.gson.Gson;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.mapping.ProfileDataException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by killpack on 27.05.15.
 */
public class BuilderTest {

//    @Test
//    public void testSingleSql() throws Exception, ProfileDataException {
//        BuilderFactory builderFactory = new BuilderFactory();
//        App app = null;
//        Gson gson = new Gson();
//        InputStreamReader settingsReader = new InputStreamReader(getClass().getResourceAsStream("/testSingleSql.json"));
//        app = gson.fromJson(settingsReader, App.class);
//        InputStreamReader profileReader = new InputStreamReader(getClass().getResourceAsStream("/reqest_example.json"));
//        Profile profile = JacksonUtil.getObjectMapper().readValue(profileReader, Profile.class);
//        builderFactory.updateConfig(app);
//        GenericKeyedObjectPool<String, SQLBuilder> factoryPool = new GenericKeyedObjectPool<String, SQLBuilder>(builderFactory);
//        SQLBuilder sqlBuilder = factoryPool.borrowObject(Utils.getFullEventName(profile));
//        sqlBuilder.maxButch = 1;
//        String sql = sqlBuilder.getResult(profile);
//        Assert.assertEquals("INSERT INTO logs VALUES('http://habrahabr.ru/company/sebbia/blog/243537/')", sql);
//    }
//
//    @Test
//    public void testBatch() throws Exception, ProfileDataException {
//        BuilderFactory builderFactory = new BuilderFactory();
//        App app = null;
//        Gson gson = new Gson();
//        InputStreamReader settingsReader = new InputStreamReader(getClass().getResourceAsStream("/testBatchSql.json"));
//        app = gson.fromJson(settingsReader, App.class);
//        InputStreamReader profileReader = new InputStreamReader(getClass().getResourceAsStream("/reqest_example.json"));
//        Profile profile = JacksonUtil.getObjectMapper().readValue(profileReader, Profile.class);
//        builderFactory.updateConfig(app);
//        GenericKeyedObjectPool<String, SQLBuilder> factoryPool = new GenericKeyedObjectPool<String, SQLBuilder>(builderFactory);
//        SQLBuilder sqlBuilder = factoryPool.borrowObject(Utils.getFullEventName(profile));
//        Assert.assertEquals(sqlBuilder.getEvent(), Utils.getFullEventName(profile));
//        sqlBuilder.maxButch = 2;
//        sqlBuilder.batch(profile);
//        sqlBuilder.batch(profile);
//        String sql = sqlBuilder.getResultIfNeed();
//        Assert.assertEquals("INSERT INTO logs VALUES ('http://habrahabr.ru/company/sebbia/blog/243537/'),\n" +
//                "('http://habrahabr.ru/company/sebbia/blog/243537/')", sql);
//    }
//
//    @Test
//    public void testBatchIncorrect() throws IOException {
//        App app = null;
//        Gson gson = new Gson();
//        InputStreamReader settingsReader = new InputStreamReader(getClass().getResourceAsStream("/testBatchSqlFail.json"));
//        app = gson.fromJson(settingsReader, App.class);
//        InputStreamReader profileReader = new InputStreamReader(getClass().getResourceAsStream("/reqest_example.json"));
//        Profile profile = JacksonUtil.getObjectMapper().readValue(profileReader, Profile.class);
//        BuilderFactory builderFactory = new BuilderFactory();
//        builderFactory.updateConfig(app);
//        GenericKeyedObjectPool<String, SQLBuilder> factoryPool = new GenericKeyedObjectPool<String, SQLBuilder>(builderFactory);
//        try {
//            SQLBuilder sqlBuilder = factoryPool.borrowObject(Utils.getFullEventName(profile));
//            sqlBuilder.batch(profile);
//            sqlBuilder.batch(profile);
//        } catch (Exception e) {
//            Assert.assertEquals("Invalid query template: you try to use batching, but did not specify repeating part(*{<repeat part>}*) of the template", e.getMessage());
//        } catch (ProfileDataException e) {
//            e.printStackTrace();
//        }
//    }
//
//    class TestBuilder extends Builder<String> {
//
//        @Override
//        protected String processField(String previousResult, String key, Object src) {
//            return null;
//        }
//    }
//
//    @Test
//    public void testNullType() throws ProfileDataException {
//        TestBuilder testFactory = new TestBuilder();
//        String time = RandomStringUtils.randomAlphanumeric(12);
//        String url = RandomStringUtils.randomNumeric(10);
//        ProfileFactory.MutableProfile profile = ProfileFactory.buildProfile();
//        Event event = new Event();
//        ObjectNode data = JsonNodeFactory.instance.objectNode();
//        event.setData(data);
//        data.putPOJO("url", url);
//        data.putPOJO("time", time);
//        profile.withSession("section").withEvent(event);
//        profile.getSessions().get(0).setCollectApp("collectApp");
//
//        FieldsEntry fieldsEntry =new FieldsEntry();
//        fieldsEntry.setFieldName("url");
//        JsonNode object = JsonNodeFactory.instance.textNode("collectApp/section/url");
//        fieldsEntry.setValueRef(object);
//        Assert.assertNull(testFactory.getValue(profile, fieldsEntry));
//    }
//
//    @Test
//    public void testType() throws ProfileDataException {
//        TestBuilder testFactory = new TestBuilder();
//        String time = RandomStringUtils.randomAlphanumeric(12);
//        String url = RandomStringUtils.randomNumeric(10);
//        ProfileFactory.MutableProfile profile = ProfileFactory.buildProfile();
//        Event event = new Event();
//        ObjectNode data = JsonNodeFactory.instance.objectNode();
//        event.setData(data);
//        event.setDefinitionId("eventDefinition");
//        data.putPOJO("url", url);
//        data.putPOJO("time", time);
//        profile.withSession("section").withEvent(event);
//        profile.getSessions().get(0).setCollectApp("collectApp");
//        FieldsEntry fieldsEntry =new FieldsEntry();
//        fieldsEntry.setType(DataLevel.EVENT_DATA.name());
//        fieldsEntry.setFieldName("url");
//        JsonNode object = JsonNodeFactory.instance.textNode("url");
//        fieldsEntry.setValueRef(object);
//        Assert.assertNotNull(testFactory.getValue(profile, fieldsEntry));
//        Assert.assertEquals(url, testFactory.getValue(profile, fieldsEntry));
//
//        fieldsEntry.setType(DataLevel.SESSION_DATA.name());
//        profile.getSessions().get(0).setData(data);
//        Assert.assertEquals(url, testFactory.getValue(profile, fieldsEntry));
//
//        fieldsEntry.setType(DataLevel.ATTRIBUTE_DATA.name());
//        Attribute attribute =new Attribute();
//        attribute.setData(data);
//        attribute.setCollectApp("collectApp");
//        attribute.setSection("section");
//        profile.getAttributes().add(attribute);
//        JsonNode object2 = JsonNodeFactory.instance.textNode("collectApp/section/url");
//        fieldsEntry.setValueRef(object2);
//        Assert.assertEquals(url, testFactory.getValue(profile, fieldsEntry));
//        fieldsEntry.setType(DataLevel.STATIC.name());
//        fieldsEntry.setValueRef(new TextNode(url));
//        Assert.assertEquals(url, testFactory.getValue(profile, fieldsEntry));
//    }
//
//    @Test
//    public void testTypeDate() throws ProfileDataException, ParseException {
//        TestBuilder testFactory = new TestBuilder();
//        String time ="2015-01-01 11:11:11";
//        ProfileFactory.MutableProfile profile = ProfileFactory.buildProfile();
//        Event event = new Event();
//        ObjectNode data = JsonNodeFactory.instance.objectNode();
//        event.setData(data);
//        data.putPOJO("time", time);
//        profile.withSession("section").withEvent(event);
//        profile.getSessions().get(0).setCollectApp("collectApp");
//        FieldsEntry fieldsEntry =new FieldsEntry();
//        fieldsEntry.setType(DataLevel.EVENT_DATA.name());
//        fieldsEntry.setFieldName("url");
//        ObjectNode fieldSettings = JsonNodeFactory.instance.objectNode();
//        fieldSettings.put("convertType","Date");
//        String timeFormat = "yyyy-MM-dd HH:mm:ss";
//        fieldSettings.put("timeFormat",timeFormat);
//        JsonNode object = new TextNode("time");
//        fieldsEntry.setFieldSettings(fieldSettings);
//        fieldsEntry.setValueRef(object);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
//        Assert.assertNotNull(testFactory.getValue(profile, fieldsEntry));
//        Assert.assertEquals(simpleDateFormat.parse(time), testFactory.getValue(profile, fieldsEntry));
//    }
}
