package com.humansapp.humans.rest;

/**
 * Created by jordan on 2014-08-14.
 */

import com.loopj.android.http.*;

public class HumansRestClient {
    private static HumansRestClient instance = null;

    private final String USER_FILE = "user_file.json";

    private AsyncHttpClient client;
    private String baseURL;

    private String userId;

    protected HumansRestClient() {
        this.client = new AsyncHttpClient();
        this.baseURL = "http://humansapp.com:4444/";
    }

    public static HumansRestClient instance() {
        if(instance == null) {
            instance = new HumansRestClient();
        }

        return instance;
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return baseURL + relativeUrl;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
