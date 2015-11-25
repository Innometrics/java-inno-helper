package com.innometrics.integrationapp;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.innometrics.cache.Cache;
import com.innometrics.cache.guava.GuavaMemoryCache;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.authentication.AppKey;
import com.innometrics.integrationapp.authentication.AuthMethod;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.innometrics.integrationapp.constants.Resources.*;

public class InnoHelper implements Serializable {
    private static final Logger logger = Logger.getLogger(InnoHelper.class.getCanonicalName());
    private static final String API_VERSION = "v1";
    private final Cache<Object> httpObjectCache;
    private final URL hostWithVersion;
    private NativeHttpClient httpClient = new NativeHttpClient();
    private final ConcurrentMap<String, String> headers = new ConcurrentHashMap<String, String>();
    private final ConcurrentMap<ProfileCloudOptions, String> parameters = new ConcurrentHashMap<ProfileCloudOptions, String>();

    public static final int DEFAULT_PORT = 80;
    public static final int DEFAULT_TTL = 300;
    public static final int DEFAULT_SIZE = 1000;
    private static volatile long REQ_DELAY = 0;
    private static volatile long LAST_REQ_TS = 0;
    Map<String,String> config;

    public InnoHelper(Map<String, String> config) throws MalformedURLException {  // todo  validate ,delete constructors
        this(config.get(InnoHelperUtils.API_SERVER),DEFAULT_PORT,new AppKey(config.get(InnoHelperUtils.APP_KEY)));
        this.config = config;
    }

    public InnoHelper(String host, int port, int cacheSize, int cacheTTL) throws MalformedURLException {
        this(host, port, null, cacheSize, cacheTTL);
    }

    public InnoHelper(String host) throws MalformedURLException {
        this(host, DEFAULT_PORT, DEFAULT_SIZE, DEFAULT_TTL);
    }

    public InnoHelper(String host, int port) throws MalformedURLException {
        this(host, port, DEFAULT_SIZE, DEFAULT_TTL);
    }

    public InnoHelper(String host, int port, AuthMethod auth) throws MalformedURLException {
        this(host, port, auth, DEFAULT_SIZE, DEFAULT_TTL);
    }


    public InnoHelper(String host, int port, AuthMethod auth, int cacheSize, int cacheTTL) throws MalformedURLException {
        if (!host.startsWith("http")) {
            host = "http://" + host;
        }
        this.hostWithVersion = new URL(host + ":" + port + "/" + API_VERSION);
        this.httpObjectCache = new GuavaMemoryCache<Object>(cacheSize, cacheTTL, Cache.Strategy.EXPIRE_AFTER_WRITE);
        withHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
        withHeader(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.toString());
        withAuth(auth);
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
        return getObject(new RestURI(hostWithVersion)
                .withResource(companies, config.get(InnoHelperUtils.COMPANY_ID))
                .withResource(buckets,  config.get(InnoHelperUtils.BUCKET_ID))
                .withResource(apps,  config.get(InnoHelperUtils.APP_ID)), App.class, null, null);
    }



    public Profile getProfile( String profileId) throws InterruptedException, ExecutionException {
        return getProfile(config.get(InnoHelperUtils.COMPANY_ID),config.get(InnoHelperUtils.BUCKET_ID), profileId, null, null);
    }

    public Profile getProfile(String companyId, String bucketId, String profileId, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws InterruptedException, ExecutionException {
        return getObject(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(profiles, profileId), Profile.class, false, tmpParams, tmpHeaders);
    }

    public FutureTask<Pair<Integer, Profile>> updateProfile(String companyId, String bucketId, Profile profile) throws ExecutionException {
        return updateProfile(companyId, bucketId, profile, null, null);
    }

    public FutureTask<Pair<Integer, Profile>> updateProfile(String companyId, String bucketId, Profile profile, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws ExecutionException {
        return postObject(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(profiles, profile.getId()), profile, Profile.class, tmpParams, tmpHeaders);
    }

    public FutureTask<Pair<Integer, Profile>> createProfile( Profile profile) throws ExecutionException {
        return createProfile(config.get(InnoHelperUtils.COMPANY_ID),config.get(InnoHelperUtils.BUCKET_ID), profile, null, null);
    }

    // todo
    public FutureTask<Pair<Integer, Profile>> createProfile(String companyId, String bucketId, Profile profile, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws ExecutionException {
        return postObject(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResources(profiles), profile, Profile.class, tmpParams, tmpHeaders);
    }

    public FutureTask<Pair<Integer, Profile>> mergeProfile(String companyId, String bucketId, String canonicalProfile, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders, String... tempProfiles) throws ExecutionException{
        Profile mergeProfile = new Profile();
        mergeProfile.setId(canonicalProfile);
        return postObject(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResource(profiles, canonicalProfile), mergeProfile, Profile.class, tmpParams, tmpHeaders);
    }

    public FutureTask<Pair<Integer, Profile>> mergeProfile(String companyId, String bucketId, String canonicalProfile, String... tempProfiles) throws ExecutionException {
        return mergeProfile(companyId, bucketId, canonicalProfile, null, null, tempProfiles);
    }

    public Segment[] getSegments(String companyId, String bucketId, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws InterruptedException, ExecutionException {
        return getObject(new RestURI(hostWithVersion)
                .withResource(companies, companyId)
                .withResource(buckets, bucketId)
                .withResources(segments), Segment[].class, tmpParams, tmpHeaders);
    }

    public Segment[] getSegments(String companyId, String bucketId) throws InterruptedException, ExecutionException {
        return getSegments(companyId, bucketId, null, null);
    }


    public Segment getSegment(String segmentId, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws InterruptedException, ExecutionException {
        return getObject(new RestURI(hostWithVersion)
                .withResource(companies, config.get(InnoHelperUtils.COMPANY_ID))
                .withResource(buckets, config.get(InnoHelperUtils.BUCKET_ID))
                .withResource(segments, segmentId), Segment.class, tmpParams, tmpHeaders);
    }

    public Segment getSegment(String segmentId) throws InterruptedException, ExecutionException {
        return getSegment( segmentId, null, null);
    }
    public IqlResult[] evaluateProfile(String companyId, String bucketId, String profileId, boolean doFiltering, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws InterruptedException, ExecutionException,  IqlSyntaxException {
        Segment[] segments = getSegments(companyId, bucketId, tmpParams, tmpHeaders);
        if (segments.length > 0) {
            Profile toEvaluate = getProfile(companyId, bucketId, profileId, tmpParams, tmpHeaders);
            IqlResult[] toReturn = new IqlResult[segments.length];
            for (int i = 0; i < segments.length; i++) {
                toReturn[i] = evaluateProfile(toEvaluate, segments[i], doFiltering);
            }
            return toReturn;
        } else {
            return new IqlResult[0];
        }
    }


    public IqlResult evaluateProfile(String companyId, String bucketId, String profileId, String segmentId, boolean doFiltering, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws InterruptedException, ExecutionException, IqlSyntaxException {
        return evaluateProfile(getProfile(companyId, bucketId, profileId, tmpParams, tmpHeaders), getSegment( segmentId, tmpParams, tmpHeaders), doFiltering);
    }

    public IqlResult evaluateProfile( String profileId, String segmentId, boolean doFiltering) throws InterruptedException, ExecutionException, IqlSyntaxException {
        return evaluateProfile(getProfile( profileId), getSegment( segmentId), doFiltering);
    }

    public IqlResult evaluateProfile(Profile profile, Segment segment, boolean doFiltering) throws  IqlSyntaxException {
        assert profile != null;
        assert segment != null && segment.getIql() != null && segment.getIql().length() > 0;
        return SegmentUtil.getIqlResult(segment.getIql(), profile, doFiltering);
    }

    private <T> T getObject(RestURI url, Class<T> tClass, boolean cache, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws ExecutionException, InterruptedException {
        Object toReturn = null;
        URL endpoint = buildWithParams(url, tmpParams);
        if (cache) toReturn = httpObjectCache.get(endpoint.toString());
        if (toReturn == null) {
            FutureTask<Pair<Integer, T>> result = process(endpoint, HttpMethods.GET, null, tClass, tmpHeaders);
            toReturn = result.get().getRight();
            if (cache) httpObjectCache.put(endpoint.toString(), toReturn);
        }
        return tClass.cast(toReturn);
    }

    private <T> T getObject(RestURI url, Class<T> tClass, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws ExecutionException, InterruptedException {
        return getObject(url, tClass, true, tmpParams, tmpHeaders);
    }

    private <T > FutureTask<Pair<Integer, T>> postObject(RestURI url, Object toUpdate, Class<T> toCast, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws  ExecutionException {
        if (toUpdate != null) {
            URL endpoint = buildWithParams(url, tmpParams);
            httpObjectCache.clearByKey(endpoint.toString());
            return process(endpoint, HttpMethods.POST, InnoHelperUtils.getGson().toJson(toUpdate), toCast, tmpHeaders);
        } else {
            throw new UnsupportedOperationException(HttpMethods.POST + " operation does not support NULL!");
        }

    }

    private <T > FutureTask<Pair<Integer, T>> putObject(RestURI url, Object toUpdate, Class<T> toCast, Map<ProfileCloudOptions, String> tmpParams, Map<String, String> tmpHeaders) throws  ExecutionException {
        if (toUpdate != null) {
            URL endpoint = buildWithParams(url, tmpParams);
            httpObjectCache.clearByKey(endpoint.toString());
            return process(endpoint, HttpMethods.PUT, InnoHelperUtils.getGson().toJson(toUpdate), toCast, tmpHeaders);
        } else {
            throw new UnsupportedOperationException(HttpMethods.PUT + " operation does not support NULL!");
        }
    }

    private <T> FutureTask<Pair<Integer, T>> process(URL orig, String method, Class<T> toCast, Map<String, String> tmpHeaders) {
        return process(orig, method, null, toCast, tmpHeaders);
    }

    private <T> FutureTask<Pair<Integer, T>> process(URL orig, String method, String body, Class<T> toCast, Map<String, String> tmpHeaders) {
        delay();
        try {
            Map<String, String> execHeader;
            if (tmpHeaders != null && tmpHeaders.size() > 0) {
                execHeader = new HashMap<String, String>();
                execHeader.putAll(this.headers);
                execHeader.putAll(tmpHeaders);
            } else {
                execHeader = this.headers;
            }
            return httpClient.sendHttpRequestAsync(orig, method, body, execHeader, toCast);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private URL buildWithParams(RestURI uri, Map<ProfileCloudOptions, String> tmpParams) {
        Map<ProfileCloudOptions, String> execParameters;
        if (tmpParams != null && tmpParams.size() > 0) {
            execParameters = new HashMap<ProfileCloudOptions, String>();
            execParameters.putAll(this.parameters);
            execParameters.putAll(tmpParams);
        } else {
            execParameters = this.parameters;
        }
        try {
            return uri.build(execParameters);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T>  T getCustom(String key, Class<T> aClass) throws ExecutionException, InterruptedException {
        return InnoHelperUtils.getGson().fromJson(getCustom(key),aClass);
    }

    public JsonElement getCustom(String key) throws ExecutionException, InterruptedException {
        return  getApp().getCustom().get(key);
    }
}
