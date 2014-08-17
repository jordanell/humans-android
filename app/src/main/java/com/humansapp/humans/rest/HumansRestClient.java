package com.humansapp.humans.rest;

/**
 * Created by jordan on 2014-08-14.
 */

import android.content.Context;

import com.google.gson.Gson;
import com.humansapp.HumansApplication;
import com.humansapp.humans.models.User;
import com.loopj.android.http.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class HumansRestClient {
    private static HumansRestClient instance = null;

    private final String USER_FILE = "user_file.json";

    private AsyncHttpClient client;
    private String baseURL;

    private User user;

    protected HumansRestClient() {
        this.client = new AsyncHttpClient();

        if(HumansApplication.isDebuggable()) {
            this.baseURL = "http://192.168.0.105:4444/";
        } else {
            this.baseURL = "http://api.humansapp.com:4444/";
        }
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

    private String getAbsoluteUrl(String relativeUrl) {
        return baseURL + relativeUrl;
    }

}
