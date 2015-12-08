package com.innometrics.integrationapp;

import com.google.gson.JsonElement;
import com.innometrics.integrationapp.authentication.AppKey;
import com.innometrics.integrationapp.authentication.AuthMethod;
import com.innometrics.integrationapp.constants.HttpHeaders;
import com.innometrics.integrationapp.constants.ProfileCloudOptions;
import com.innometrics.integrationapp.httpclient.NativeHttpClient;
import com.innometrics.integrationapp.model.App;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.model.Segment;
import com.innometrics.integrationapp.utils.HttpMethods;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import com.innometrics.integrationapp.utils.RestURI;
import com.innometrics.integrationapp.utils.SegmentUtil;
import com.innometrics.iql.IqlResult;
import com.innometrics.iql.IqlSyntaxException;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.innometrics.integrationapp.constants.Resources.*;
import static com.innometrics.integrationapp.utils.InnoHelperUtils.*;

public class InnoHelper implements Serializable {
    private static final Logger logger = Logger.getLogger(InnoHelper.class.getCanonicalName());
    private static final String API_VERSION = "v1";
    private final URL hostWithVersion;
    private NativeHttpClient httpClient = new NativeHttpClient();
    private final ConcurrentMap<String, String> headers = new ConcurrentHashMap<String, String>();
    private final ConcurrentMap<ProfileCloudOptions, String> parameters = new ConcurrentHashMap<ProfileCloudOptions, String>();

    public static final String DEFAULT_PORT = "80";
    public static final String DEFAULT_TTL = "300";
    public static final String DEFAULT_SIZE = "1000";
    private static volatile long REQ_DELAY = 0;
    private static volatile long LAST_REQ_TS = 0;
    private long lastGetConfigTime;
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

    public InnoHelper(Map<String, String> config) throws MalformedURLException {
        host =getOrError(config,API_SERVER);
        appKey = getOrError(config, APP_KEY);
        appID = getOrError(config, APP_ID);
        bucketId = getOrError(config, BUCKET_ID);
        companyId = getOrError(config,COMPANY_ID);
        port = Integer.valueOf(config.getOrDefault(API_PORT, DEFAULT_PORT));
        cacheSize = Integer.valueOf(config.getOrDefault(API_PORT, DEFAULT_SIZE));
        cacheTTL = Integer.valueOf(config.getOrDefault(API_PORT, DEFAULT_TTL));
        if (!host.startsWith("http")) {
            host = "http://" + host;
        }
        this.hostWithVersion = new URL(host + ":" + port + "/" + API_VERSION);
        withHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        withHeader(HttpHeaders.ACCEPT, "application/json; charset=utf-8");
        withAuth(new AppKey(appKey));
    }

    String getOrError(Map<String, String> config,String field){
        if (!config.containsKey(field)){
            throw new IllegalArgumentException("In the settings missing a required field "+ field);
        }
        else return config.get(field);
    }
    public InnoHelper withAuth(AuthMethod auth) {
        if (auth != null) {
            auth.authorize(parameters, headers);
        }
        return this;
    }

    public InnoHelper withHeader(String header, String value) {
        this.headers.put(header, value);
        return this;
    }

    public InnoHelper withParameter(ProfileCloudOptions key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    public void setMaxRequestsPerSecond(int rps) {
        setDelayMS(1000 / rps);
    }

    public void setDelayMS(long rps) {
        REQ_DELAY = rps;
        logger.info("Set global request delay of " + REQ_DELAY + "ms");
    }

    private void delay() {
        if (REQ_DELAY > 0) {
            long toSleep = (LAST_REQ_TS + REQ_DELAY) - System.currentTimeMillis();
            if (toSleep > 0) {
                try {
                    logger.log(Level.FINE, "Last request was done at " + LAST_REQ_TS + " Sleeping for " + toSleep + "ms (delay per request is " + REQ_DELAY + "ms)");
                    Thread.sleep(toSleep);
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "Interrupted!", e);
                }
            }
            LAST_REQ_TS = System.currentTimeMillis();
        }
    }

    public App getApp() throws ExecutionException, InterruptedException {
        if (lastGetConfigTime + getConfigTimeOut < System.currentTimeMillis() ) {
            app = getObject(new RestURI(hostWithVersion).withResource(companies, companyId).withResource(buckets, bucketId).withResource(apps, appID), App.class).get().getRight();
            lastGetConfigTime= System.currentTimeMillis();
        }
        return app;
    }

//    public FutureTask<Pair<Integer, App>> getApp() throws ExecutionException, InterruptedException {
//        return getObject(new RestURI(hostWithVersion)
//                .withResource(companies, companyId)
//                .withResource(buckets, bucketId)
//                .withResource(apps, appID), App.class);
//    }

    public FutureTask<Pair<Integer, Profile>> getProfile(String profileId) throws InterruptedException, ExecutionException {
        return getObject(new RestURI(hostWithVersion).withResource(companies, companyId).withResource(buckets, bucketId).withResource(profiles, profileId), Profile.class);
    }

    public FutureTask<Pair<Integer, Profile>> saveProfile(Profile profile) throws ExecutionException {
        return postObject(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(profiles, profile.getId()), profile, Profile.class);
    }

    public FutureTask<Pair<Integer, Profile>> mergeProfile(String companyId, String bucketId, String canonicalProfile, String... tempProfiles) throws ExecutionException {
        Profile mergeProfile = new Profile();
        mergeProfile.setId(canonicalProfile);
        mergeProfile.setMergedProfiles(new HashSet<String>(Arrays.asList(tempProfiles)));
        return postObject(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(profiles, canonicalProfile), mergeProfile, Profile.class);
    }

    public FutureTask<Pair<Integer, Segment[]>> getSegments() throws InterruptedException, ExecutionException {
        return getObject(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResources(segments), Segment[].class);
    }

    public FutureTask<Pair<Integer, Segment>> getSegment(String segmentId) throws InterruptedException, ExecutionException {
        return getObject(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(segments, segmentId), Segment.class);
    }

    // todo
    public IqlResult[] evaluateProfile(String profileId, boolean doFiltering) throws InterruptedException, ExecutionException, IqlSyntaxException {
        Segment[] segments = getSegments().get().getRight();
        if (segments.length > 0) {
            Profile toEvaluate = getProfile(profileId).get().getRight();
            IqlResult[] toReturn = new IqlResult[segments.length];
            for (int i = 0; i < segments.length; i++) {
                toReturn[i] = evaluateProfile(toEvaluate, segments[i], doFiltering);
            }
            return toReturn;
        } else {
            return new IqlResult[0];
        }
    }

//    public IqlResult evaluateProfile(String profileId, String segmentId, boolean doFiltering) throws InterruptedException, ExecutionException, IqlSyntaxException {
//        return evaluateProfile(getProfile(profileId), getSegment(segmentId), doFiltering);
//    }

    public IqlResult evaluateProfile(Profile profile, Segment segment, boolean doFiltering) throws IqlSyntaxException {
        assert profile != null;
        assert segment != null && segment.getIql() != null && segment.getIql().length() > 0;
        return SegmentUtil.getIqlResult(segment.getIql(), profile, doFiltering);
    }

    private <T> FutureTask<Pair<Integer, T>> getObject(RestURI url, Class<T> tClass) throws ExecutionException, InterruptedException {
        URL endpoint = build(url);
        return process(endpoint, HttpMethods.GET, null, tClass);
    }


    private <T> FutureTask<Pair<Integer, T>> postObject(RestURI url, Object toUpdate, Class<T> toCast) throws ExecutionException {
        if (toUpdate != null) {
            URL endpoint = build(url);
            return process(endpoint, HttpMethods.POST, InnoHelperUtils.getGson().toJson(toUpdate), toCast);
        } else {
            throw new UnsupportedOperationException(HttpMethods.POST + " operation does not support NULL!");
        }

    }

    private <T> FutureTask<Pair<Integer, T>> process(URL orig, String method, String body, Class<T> toCast) {
        delay();
        try {
            return httpClient.sendHttpRequestAsync(orig, method, body, headers, toCast);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private URL build(RestURI uri) {
        Map<ProfileCloudOptions, String> execParameters;
        execParameters = this.parameters;
        try {
            return uri.build(execParameters);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getCustom(String key, Class<T> aClass) throws ExecutionException, InterruptedException {
        return InnoHelperUtils.getGson().fromJson(getCustom(key), aClass);
    }

    public JsonElement getCustom(String key) throws ExecutionException, InterruptedException {
        return getApp().getCustom().get(key);
    }


    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getCacheTTL() {
        return cacheTTL;
    }

    public void setCacheTTL(int cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    public void setHttpClient(NativeHttpClient httpClient) {
        this.httpClient = httpClient;
    }

}
