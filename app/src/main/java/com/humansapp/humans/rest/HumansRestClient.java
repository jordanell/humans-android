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
    private final X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");

    private static HumansRestClient instance = null;

    private AsyncHttpClient client;
    private String baseURL;

    protected HumansRestClient() {
        this.client = new AsyncHttpClient();

        if(isDebuggable(HumansApplication.getAppContext())) {
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

    private boolean isDebuggable(Context ctx)
    {
        boolean debuggable = false;

        try
        {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for ( int i = 0; i < signatures.length;i++)
            {
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable)
                    break;
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            //debuggable variable will remain false
        }
        catch (CertificateException e)
        {
            //debuggable variable will remain false
        }
        return debuggable;
    }
}
