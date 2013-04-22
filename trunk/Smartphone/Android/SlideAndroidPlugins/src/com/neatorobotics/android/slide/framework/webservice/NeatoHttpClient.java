package com.neatorobotics.android.slide.framework.webservice;


import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;



public class NeatoHttpClient {
	private static final int SOCKET_TIMEOUT_MILLIS = 60 * 1000; // 60 seconds
	private static final int CONNECTIION_TIMEOUT_MILLIS = 60 * 1000; // 60 seconds
	
	private static DefaultHttpClient mClient;
	
	private static final synchronized HttpClient get (Context context) {
		if (mClient == null) {
	        HttpParams params = new BasicHttpParams();

	        HttpConnectionParams.setStaleCheckingEnabled(params, false);

	        HttpConnectionParams.setConnectionTimeout(params, CONNECTIION_TIMEOUT_MILLIS);
	        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MILLIS);
	        HttpConnectionParams.setSocketBufferSize(params, 8192);
	        HttpClientParams.setRedirecting(params, false);
	        SSLSessionCache sessionCache = (context != null) ? new SSLSessionCache(context) : null;
	        HttpProtocolParams.setContentCharset(params, "UTF-8");
	        SchemeRegistry schemeRegistry = new SchemeRegistry();
	        schemeRegistry.register(new Scheme("http",
	                PlainSocketFactory.getSocketFactory(), 80));
	        schemeRegistry.register(new Scheme("https",
	                SSLCertificateSocketFactory.getHttpSocketFactory(
	                		SOCKET_TIMEOUT_MILLIS, sessionCache), 443));

	        ClientConnectionManager manager =
	                new ThreadSafeClientConnManager(params, schemeRegistry);
	        
	        mClient = new DefaultHttpClient(manager, params);
		}
		
		return mClient;
	}
	
    public static HttpResponse execute(Context context, HttpGet get) throws IOException {
        return get(context).execute(get);
    }
    
    public static HttpResponse execute(Context context, HttpPost post) throws IOException {
        return get(context).execute(post);
    }
    
    public static String execute(Context context, HttpGet get, ResponseHandler<String> responseHandler) throws IOException {
        return get(context).execute(get, responseHandler);
    }
    
    public static String execute(Context context, HttpPost post, ResponseHandler<String> responseHandler) throws IOException {
        return get(context).execute(post, responseHandler);
    }	
}
