package com.innometrics.integrationapp.httpclient;


import com.google.common.net.HttpHeaders;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.innometrics.integrationapp.InnoHelperUtils;
import com.innometrics.integrationapp.utils.HttpMethods;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class NativeHttpClient implements Serializable {
    private volatile ExecutorService executor;

    public <T> FutureTask<Pair<Integer, T>> sendHttpRequestAsync(URL url, String method, String body, Map<String, String> headers, Class<? extends T> resultClass) throws MalformedURLException {
        FutureTask<Pair<Integer, T>> toReturn = new FutureTask<Pair<Integer, T>>(getCallable(url, method, body, headers, resultClass));
        getExecutor().execute(toReturn);
        return toReturn;
    }

    private static <T> Callable<Pair<Integer, T>> getCallable(URL url, String method, String body, Map<String, String> headers, Class<? extends T> resultClass) {
        return new RequestRunner<T>(url, method, body, headers, resultClass);
    }

    private synchronized ExecutorService getExecutor() {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    static class RequestRunner<T> implements Callable<Pair<Integer, T>> {
        private final URL url;
        private final String method;
        private final String body;
        private final Map<String, String> headers;
        private final Class<? extends T> resultClass;

        RequestRunner(URL url, String method, String body, Map<String, String> headers, Class<? extends T> resultClass) {
            this.url = url;
            this.method = method;
            this.body = body;
            this.headers = headers;
            this.resultClass = resultClass;
        }


        @Override
        public Pair<Integer, T> call() throws IOException {
            HttpURLConnection connection = null;
            try {

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                //Headers
                for (String headerKey : headers.keySet()) {
                    connection.setRequestProperty(headerKey, headers.get(headerKey));
                }

                if (!method.equalsIgnoreCase(HttpMethods.GET) && StringUtils.isNotEmpty(body)) {
                    connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, Integer.toString(body.getBytes().length));
                    connection.setDoOutput(true);
                    IOUtils.write(body, connection.getOutputStream());
                }
                if (connection.getInputStream() != null) {
                    String resultString = IOUtils.toString(connection.getInputStream());
                    T resultObj = null;
                    if (!StringUtils.isEmpty(resultString)) {
                        JsonParser jsonParser = new JsonParser();
                        JsonObject container = (JsonObject) jsonParser.parse(resultString);
                        String fieldName = resultClass.getSimpleName().toLowerCase();
                        if (container.has(fieldName)) {
                            resultObj = InnoHelperUtils.getGson().fromJson(container.get(fieldName), resultClass);
                        }
                    }
                    return new ImmutablePair<Integer, T>(connection.getResponseCode(), resultObj);
                } else {
                    throw new IOException("Empty return data.");
                }
            } catch (IOException ex) {
                String message = ex.getMessage();
                if (connection != null && connection.getErrorStream() != null) {
                    String toReplace = IOUtils.toString(connection.getErrorStream());
                    if (StringUtils.trimToNull(toReplace) != null) {
                        message = toReplace;
                    }
                }
                throw new IOException("Error issue " + method + " to URL:" + url + " Payload:" + body + " Message:" + message, ex.getCause());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }
}
