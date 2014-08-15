package com.humansapp.humans.rest;

/**
 * Created by jordan on 2014-08-14.
 */

import com.humansapp.HumansApplication;
import com.loopj.android.http.*;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

public class HumansRestClient {
    private static HumansRestClient instance = null;

    private AsyncHttpClient client;
    private String baseURL;

    protected HumansRestClient() {
        this.client = new AsyncHttpClient();

        if(HumansApplication.isDebuggable()) {
            this.baseURL = "http://localhost:4444/";
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
