package com.neatorobotics.android.slide.framework.webservice;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;

public class NeatoHttpClient {
    private static final int SOCKET_TIMEOUT_MILLIS = 60 * 1000; // 60 seconds
    private static final int CONNECTIION_TIMEOUT_MILLIS = 60 * 1000; // 60
                                                                     // seconds
    private static final int MAX_RETRY_COUNT = 3;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 5;

    private static DefaultHttpClient mClient;

    // ALLOW_ALL_CERTIFICATE if set to true, accepts all the certificates
    // **WARNING** - This should not be enabled in the production code
    // Currently our neatosecure server uses the self signed certificate, we
    // have set ALLOW_ALL_CERTIFICATE flag to true to make it work over https
    // However once we have the certificate from proper CA, then we should set
    // ALLOW_ALL_CERTIFICATE
    // to false
    private static final boolean ALLOW_ALL_CERTIFICATE = true;

    private static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
                UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    private static final synchronized HttpClient get(Context context) {

        if (ALLOW_ALL_CERTIFICATE) {
            return getHttpClientAllowAllCerts(context);
        } else {
            return getHttpClient(context);
        }
    }

    private static final synchronized HttpClient getHttpClient(Context context) {
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
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", SSLCertificateSocketFactory.getHttpSocketFactory(
                    SOCKET_TIMEOUT_MILLIS, sessionCache), 443));
            ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {
                @Override
                public int getMaxForRoute(HttpRoute httproute) {
                    return MAX_CONNECTIONS_PER_ROUTE;
                }
            });
            HttpProtocolParams.setUseExpectContinue(params, false);

            ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
            mClient = new DefaultHttpClient(manager, params);
            mClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(MAX_RETRY_COUNT, true));
        }

        return mClient;
    }

    private static final synchronized HttpClient getHttpClientAllowAllCerts(Context context) {
        if (mClient == null) {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                HttpParams params = new BasicHttpParams();

                HttpConnectionParams.setStaleCheckingEnabled(params, false);

                HttpConnectionParams.setConnectionTimeout(params, CONNECTIION_TIMEOUT_MILLIS);
                HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MILLIS);
                HttpConnectionParams.setSocketBufferSize(params, 8192);

                HttpClientParams.setRedirecting(params, false);
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                schemeRegistry.register(new Scheme("https", sf, 443));
                ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {
                    @Override
                    public int getMaxForRoute(HttpRoute httproute) {
                        return MAX_CONNECTIONS_PER_ROUTE;
                    }
                });
                HttpProtocolParams.setUseExpectContinue(params, false);

                ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
                mClient = new DefaultHttpClient(manager, params);
                mClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(MAX_RETRY_COUNT, true));
            } catch (Exception e) {

            }
        }
        return mClient;
    }

    public static HttpResponse execute(Context context, HttpGet get) throws IOException {
        return get(context).execute(get);
    }

    public static HttpResponse execute(Context context, HttpPost post) throws IOException {
        return get(context).execute(post);
    }

    public static String execute(Context context, HttpGet get, ResponseHandler<String> responseHandler)
            throws IOException {
        return get(context).execute(get, responseHandler);
    }

    public static String execute(Context context, HttpPost post, ResponseHandler<String> responseHandler)
            throws IOException {
        return get(context).execute(post, responseHandler);
    }

}
