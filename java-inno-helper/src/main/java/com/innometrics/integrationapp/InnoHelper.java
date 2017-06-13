package com.innometrics.integrationapp;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.App;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Segment;
import com.innometrics.integrationapp.scheduler.SchedulerTask;
import com.innometrics.integrationapp.scheduler.SchedulerTaskException;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import com.innometrics.integrationapp.utils.RestURI;
import com.innometrics.integrationapp.utils.SegmentUtil;
import com.innometrics.iql.IqlResult;
import com.innometrics.iql.IqlSyntaxException;
import com.squareup.okhttp.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.innometrics.integrationapp.constants.Resources.*;
import static com.innometrics.integrationapp.utils.InnoHelperUtils.*;
import static com.innometrics.integrationapp.utils.ConfigNames.*;

public class InnoHelper {
    private static final String API_VERSION = "v1";
    private final URL hostWithVersion;
    private OkHttpClient httpClient = new OkHttpClient();
    private final ConcurrentMap<String, String> parameters = new ConcurrentHashMap<String, String>();
    private static final Logger LOGGER = Logger.getLogger(InnoHelper.class);
    Set<AppConfigChangeListener> changeListeners = new HashSet<>();
    public static final String DEFAULT_PORT = "80";
    public static final String DEFAULT_TTL = "300";
    public static final String DEFAULT_SIZE = "1000";
    private volatile long lastGetConfigTime;
    volatile App app;
    String companyId;
    String fld_schedulerApiHost;
    String bucketId;
    String host;
    String appKey;
    String appID;
    int port;
    int cacheSize;
    int cacheTTL;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    JsonParser jsonParser = new JsonParser();

    public InnoHelper(Map<String, String> config) throws MalformedURLException {
        host = getOrError(config, INNO_API_HOST.name());
        appKey = getOrError(config, INNO_APP_KEY.name());
        appID = getOrError(config, INNO_APP_ID.name());
        bucketId = getOrError(config, INNO_BUCKET_ID.name());
        companyId = getOrError(config, INNO_COMPANY_ID.name());
        fld_schedulerApiHost = config.get(schedulerApiHost.name());
        port = Integer.valueOf(config.containsKey(INNO_API_PORT.name()) ? config.get(INNO_API_PORT.name()) : DEFAULT_PORT);
        cacheSize = Integer.valueOf(config.get(CACHE_SIZE) != null ? config.get(CACHE_SIZE) : DEFAULT_SIZE);
        cacheTTL = Integer.valueOf(config.get(CACHE_TTL) != null ? config.get(CACHE_TTL) : DEFAULT_TTL);
        if (!host.startsWith("http")) {
            host = "https://" + host;
        }
        httpClient.interceptors().add(new ThrottlingInterceptor(100));
        this.hostWithVersion = new URL(host + ":" + port + "/" + API_VERSION);
        this.parameters.put("app_key", appKey);
    }

    String getOrError(Map<String, String> config, String field) {
        if (!config.containsKey(field)) {
            LOGGER.error("In the settings missing a required field " + field);
            return null;
        } else return config.get(field);
    }


    public synchronized App getApp() throws IOException {
        long getConfigTimeOut = 10_000;
        if (lastGetConfigTime + getConfigTimeOut < System.currentTimeMillis()) {
            App tempApp = null;
            tempApp = getObjectSync(new RestURI(hostWithVersion).withResource(companies, companyId).withResource(buckets, bucketId).withResource(apps, appID), App.class);
            if (!(tempApp != null && tempApp.equals(app))) {
                app = tempApp;
                for (AppConfigChangeListener changeListener : changeListeners) {
                    changeListener.change(app);
                }
            }
            lastGetConfigTime = System.currentTimeMillis();
        }
        return app;
    }

    public void addChangeListeners(AppConfigChangeListener changeListener) {
        this.changeListeners.add(changeListener);
    }

    <T> T processResponse(Response response, Class<T> aClass) throws IOException {
        T result = null;
        if (response.isSuccessful()) {
            JsonObject container = null;
            container = (JsonObject) jsonParser.parse(response.body().string());
            String fieldName = aClass.getSimpleName().toLowerCase();
            if (container != null && container.has(fieldName)) {
                result = InnoHelperUtils.getGson().fromJson(container.get(fieldName), aClass);
            }
        } else {
            LOGGER.error("http error :" + response.code() + " [" + response.message() + "]");
        }
        return result;
    }

    public Profile getProfile(String profileId) throws IOException {
        return getObjectSync(new RestURI(hostWithVersion).withResource(companies, companyId).withResource(buckets, bucketId).withResource(profiles, profileId), Profile.class);
    }

    private <T> T getObjectSync(RestURI restURI, Class<T> tClass) throws IOException {
        URL endpoint = null;
        endpoint = build(restURI);
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        return processResponse(response, tClass);
    }

    private Response postObjectSync(RestURI url, Object toUpdate) throws IOException {
        if (toUpdate != null) {
            URL endpoint = null;
            endpoint = build(url);
            RequestBody requestBody = RequestBody.create(JSON, InnoHelperUtils.getGson().toJson(toUpdate));
            Request request = new Request.Builder().url(endpoint).post(requestBody).build();
            return httpClient.newCall(request).execute();
        } else {
            LOGGER.error("POST operation does not support NULL!");
            return null;
        }
    }

    public Response saveProfile(Profile profile) throws IOException {
        return postObjectSync(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(profiles, profile.getId()), profile);
    }

    public Response mergeProfile(String companyId, String bucketId, String canonicalProfile, String... tempProfiles) throws IOException {
        Profile mergeProfile = new Profile();
        mergeProfile.setId(canonicalProfile);
        mergeProfile.setMergedProfiles(new HashSet<String>(Arrays.asList(tempProfiles)));
        return postObjectSync(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(profiles, canonicalProfile), mergeProfile);
    }

    public Segment[] getSegments() throws IOException {
        return getObjectSync(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResources(segments), Segment[].class);
    }

    public Segment getSegment(String segmentId) throws IOException {
        return getObjectSync(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(segments, segmentId), Segment.class);
    }

    public IqlResult[] evaluateProfile(String profileId, boolean doFiltering) throws IOException, IqlSyntaxException {
        Segment[] segments = new Segment[0];
        segments = getSegments();
        if (segments.length > 0) {
            Profile toEvaluate = getProfile(profileId);
            IqlResult[] toReturn = new IqlResult[segments.length];
            for (int i = 0; i < segments.length; i++) {
                toReturn[i] = evaluateProfile(toEvaluate, segments[i], doFiltering);
            }
            return toReturn;
        } else {
            return new IqlResult[0];
        }
    }


    public IqlResult evaluateProfile(Profile profile, Segment segment, boolean doFiltering) throws IqlSyntaxException {
        assert profile != null;
        assert segment != null && segment.getIql() != null && segment.getIql().length() > 0;
        return SegmentUtil.getIqlResult(segment.getIql(), profile, doFiltering);
    }


    private URL build(RestURI uri) throws MalformedURLException {
        Map<String, String> execParameters;
        execParameters = this.parameters;
        return uri.build(execParameters);
    }

    public <T> T getCustom(String key, Class<T> aClass) throws IOException {
        JsonElement jsonElement = getCustom(key);
        return jsonElement != null ? InnoHelperUtils.getGson().fromJson(jsonElement, aClass) : null;
    }

    public RulesEntry[] getRulesEntries() throws IOException {
       return getCustom("rules", RulesEntry[].class);
    }

    private String getSchedulerApiUrl(String taskId) throws SchedulerTaskException {
        if (fld_schedulerApiHost==null || fld_schedulerApiHost.isEmpty()){
            throw new SchedulerTaskException("schedulerApiHost Should not be empty");
        }
        return String.format("%s/scheduler/%s%s?token=%s", fld_schedulerApiHost, getSchedulerId(), taskId != null && !taskId.isEmpty() ? '/' + taskId : "", getAppKey());
    }

    private String getSchedulerId() {
        return getCompanyId() + "-" + getBucketId() + "-" + getAppID();
    }

    public boolean addTask(SchedulerTask schedulerTask) throws SchedulerTaskException, IOException {
        if (schedulerTask.validate()) {
            String url = getSchedulerApiUrl(null);
            RequestBody requestBody = RequestBody.create(JSON, InnoHelperUtils.getGson().toJson(schedulerTask));// todo exclude null fields
            Request request = new Request.Builder().url(url).post(requestBody).build();
            Response response = httpClient.newCall(request).execute();
            processResponse(response,SchedulerTask[].class);
        }
        return true;
    }

    public JsonElement getCustom(String key) throws IOException {
        App appTmp = getApp();
        if (appTmp == null) {
            LOGGER.error("App Settings is Empty");
            return null;
        }
        return appTmp.getCustom().get(key);
    }


    public String getCompanyId() {
        return companyId;
    }

    public String getBucketId() {
        return bucketId;
    }

    public String getHost() {
        return host;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppID() {
        return appID;
    }

    public int getPort() {
        return port;
    }

}
