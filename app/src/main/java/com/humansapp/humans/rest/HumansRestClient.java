package com.humansapp.humans.rest;

/**
 * Created by jordan on 2014-08-14.
 */

import com.loopj.android.http.*;

public class HumansRestClient {
    private static HumansRestClient instance = null;

    // The preferences file
    private final String USER_FILE = "user_file.json";

    private AsyncHttpClient client;
    private String baseURL;

    private String userId;

    /**
     * Instantiates a new HumansRestClient
     */
    protected HumansRestClient() {
        this.client = new AsyncHttpClient();
        this.baseURL = "http://humansapp.com:4444/";
    }

    /**
     * Returns a singleton instance of the HumansRestClient
     * @return HumansRestClient.
     */
    public static HumansRestClient instance() {
        if(instance == null) {
            instance = new HumansRestClient();
        }

        return instance;
    }

    /**
     * Perform a GET request.
     * @param url The url to request.
     * @param params The request parameters to attach.
     * @param responseHandler The response handler to use.
     */
    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Perform a POST request.
     * @param url The url to request.
     * @param params The request parameters to attach.
     * @param responseHandler The response handler to use.
     */
    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Perform a PUT request.
     * @param url The url to request.
     * @param params The request parameters to attach.
     * @param responseHandler The response handler to use.
     */
    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Returns the absolute url to be used for a given url request.
     * @param relativeUrl The relative URL of a request.
     * @return The absolute URL.
     */
    private String getAbsoluteUrl(String relativeUrl) {
        return baseURL + relativeUrl;
    }

    /**
     * Returns the current user ID being used for requests.
     * @return The current user ID.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the current user ID to be used for requests.
     * @param userId The user ID to use for requests.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
