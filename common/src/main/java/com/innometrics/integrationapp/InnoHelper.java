package com.innometrics.integrationapp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.innometrics.integrationapp.model.App;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Segment;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import com.innometrics.integrationapp.utils.RestURI;
import com.innometrics.integrationapp.utils.SegmentUtil;
import com.innometrics.iql.IqlResult;
import com.innometrics.iql.IqlSyntaxException;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static com.innometrics.integrationapp.constants.Resources.*;
import static com.innometrics.integrationapp.utils.InnoHelperUtils.*;

public class InnoHelper implements Serializable {
    private static final Logger logger = Logger.getLogger(InnoHelper.class.getCanonicalName());
    private static final String API_VERSION = "v1";
    private final URL hostWithVersion;
    private OkHttpClient httpClient = new OkHttpClient();
    private final ConcurrentMap<String, String> parameters = new ConcurrentHashMap<String, String>();

    public static final String DEFAULT_PORT = "80";
    public static final String DEFAULT_TTL = "300";
    public static final String DEFAULT_SIZE = "1000";
    private static volatile long REQ_DELAY = 200;
    private volatile long lastGetConfigTime;
    private long getConfigTimeOut = 10_000;
    App app;
    String companyId;
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
        host = getOrError(config, API_SERVER);
        appKey = getOrError(config, APP_KEY);
        appID = getOrError(config, APP_ID);
        bucketId = getOrError(config, BUCKET_ID);
        companyId = getOrError(config, COMPANY_ID);
        port = Integer.valueOf(config.get(API_PORT) != null ? config.get(API_PORT) : DEFAULT_PORT);
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
            throw new IllegalArgumentException("In the settings missing a required field " + field);
        } else return config.get(field);
    }


    public App getApp() throws IOException, ExecutionException, InterruptedException {
        if (lastGetConfigTime + getConfigTimeOut < System.currentTimeMillis()) {
            app = getObjectSync(new RestURI(hostWithVersion).withResource(companies, companyId).withResource(buckets, bucketId).withResource(apps, appID),App.class);
            lastGetConfigTime = System.currentTimeMillis();
        }
        return app;
    }

    <T> T processResponse(Response response, Class<T> aClass) throws IOException {
        T result = null;
        if (response.isSuccessful()){
            JsonObject container = (JsonObject) jsonParser.parse(response.body().string());
            String fieldName = aClass.getSimpleName().toLowerCase();
            if (container.has(fieldName)) {
                result = InnoHelperUtils.getGson().fromJson(container.get(fieldName), aClass);
            }
        }else  {
            System.out.println("http error :" + response.code() + " [" + response.message() + "]") ;
        }
        return result;
    }

    public Profile getProfile(String profileId) throws InterruptedException, ExecutionException, IOException {
        return getObjectSync(new RestURI(hostWithVersion).withResource(companies, companyId).withResource(buckets, bucketId).withResource(profiles, profileId), Profile.class);
    }

    private <T> T getObjectSync(RestURI restURI, Class<T> tClass) throws ExecutionException, InterruptedException, IOException {
        URL endpoint = build(restURI);
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        return processResponse(response, tClass);
    }

//    private void getObjectAsync(RestURI restURI, Callback callback) throws ExecutionException, InterruptedException, IOException {
//        URL endpoint = build(restURI);
//        Request request = new Request.Builder().url(endpoint).get().build();
//        Call call = httpClient.newCall(request);
//        call.enqueue(callback);
//    }

    private Response postObjectSync(RestURI url, Object toUpdate) throws IOException {
        if (toUpdate != null) {
            URL endpoint = build(url);

            RequestBody requestBody = RequestBody.create(JSON, InnoHelperUtils.getGson().toJson(toUpdate));
            Request request = new Request.Builder().url(endpoint).post(requestBody).build();
            return httpClient.newCall(request).execute();
        } else {
            throw new UnsupportedOperationException("POST operation does not support NULL!");
        }
    }
    public Response saveProfile(Profile profile) throws  IOException {
        return postObjectSync(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(profiles, profile.getId()), profile);
    }
//
    public Response mergeProfile(String companyId, String bucketId, String canonicalProfile, String... tempProfiles) throws ExecutionException, IOException {
        Profile mergeProfile = new Profile();
        mergeProfile.setId(canonicalProfile);
        mergeProfile.setMergedProfiles(new HashSet<String>(Arrays.asList(tempProfiles)));
        return postObjectSync(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(profiles, canonicalProfile), mergeProfile);
    }
//
    public Segment[] getSegments() throws InterruptedException, ExecutionException, IOException {
        return getObjectSync(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResources(segments), Segment[].class);
    }
//
    public Segment getSegment(String segmentId) throws IOException, ExecutionException, InterruptedException {
        return getObjectSync(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(segments, segmentId), Segment.class);
    }

    // todo
    public IqlResult[] evaluateProfile(String profileId, boolean doFiltering) throws InterruptedException, ExecutionException, IqlSyntaxException, IOException {
        Segment[] segments = getSegments();
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


    private URL build(RestURI uri) {
        Map<String, String> execParameters;
        execParameters = this.parameters;
        try {
            return uri.build(execParameters);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getCustom(String key, Class<T> aClass) throws ExecutionException, InterruptedException, IOException {
        return InnoHelperUtils.getGson().fromJson(getCustom(key), aClass);
    }

    public JsonElement getCustom(String key) throws ExecutionException, InterruptedException, IOException {
        return getApp().getCustom().get(key);
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
