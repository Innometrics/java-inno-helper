package com.innometrics.integrationapp.utils;


import com.innometrics.integrationapp.constants.Resources;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author andrew, Innometrics
 */
public class RestURI {
    private final StringBuilder stringBuilder;

    public RestURI(String hostWithVersion) {
        stringBuilder = new StringBuilder(hostWithVersion);
    }

    public RestURI(URL hostWithVersion) {
        this(hostWithVersion.toString());
    }

    public RestURI withResource(Resources resourceName, String resourceValue) {
        stringBuilder.append("/").append(urlEncode(resourceName.name())).append("/").append(urlEncode(resourceValue));
        return this;
    }

    public RestURI withResources(Resources resourceName) {
        stringBuilder.append("/").append(urlEncode(resourceName.name()));
        return this;
    }

    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, Charset.defaultCharset().name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public URL build(Map<String, String> parameters) throws MalformedURLException {
        for (String paraKey : parameters.keySet()) {
            String dim = "&";
            if (stringBuilder.indexOf("?") == -1) {
                dim = "?";
            }
            stringBuilder.append(dim).append(urlEncode(paraKey)).append("=").append(urlEncode(parameters.get(paraKey)));
        }
        return new URL(stringBuilder.toString());
    }



    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
