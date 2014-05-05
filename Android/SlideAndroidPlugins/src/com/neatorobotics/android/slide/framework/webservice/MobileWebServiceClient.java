package com.neatorobotics.android.slide.framework.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.NetworkConnectionUtils;

public class MobileWebServiceClient {

    private static final String TAG = MobileWebServiceClient.class.getSimpleName();

    private static InputStream executeHttpPostAndReturnStream(Context context, String methodName,
            Map<String, String> postParams) throws IOException, UserUnauthorizedException {
        String url = getUrlFromMethodName(context, methodName);
        LogHelper.logD(TAG, "Executing URL = " + url);

        if (!NetworkConnectionUtils.hasNetworkConnection(context)) {
            LogHelper.log(TAG, "Internet connection not available");
            IOException exception = new IOException("No internet connection available");
            throw exception;
        }

        postParams.put(NeatoWebConstants.QUERY_KEY_APIKEY, NeatoWebConstants.getApiKey(context));
        HttpPost postHttpRequest = new HttpPost(url);

        postHttpRequest.addHeader("Accept-Language", AppUtils.getCurrentLocale(context));

        String deviceId = NeatoPrefs.getNeatoUserDeviceId(context);
        if (!TextUtils.isEmpty(deviceId)) {
            postHttpRequest.addHeader("X-NEATO-UUID", NeatoPrefs.getNeatoUserDeviceId(context));
        }

        String authKey = NeatoPrefs.getNeatoUserAuthToken(context);
        if (!TextUtils.isEmpty(authKey)) {
            postHttpRequest.addHeader("X-NEATO-SESSION-ID", authKey);
        }

        String headerTrack = NeatoHttpHeaderUtils.getNeatoHttpHeaderString(context);
        if (!TextUtils.isEmpty(headerTrack)) {
            postHttpRequest.addHeader("X-NEATO-APPINFO", headerTrack);
        }

        final List<NameValuePair> pairs = new ArrayList<NameValuePair>();

        for (Entry<String, String> params : postParams.entrySet()) {
            pairs.add(new BasicNameValuePair(params.getKey(), params.getValue()));
            LogHelper.logD(TAG, "Key = " + params.getKey() + " Value = " + params.getValue());
        }

        HttpEntity responseEntity = null;
        InputStream responseInputStream = null;

        postHttpRequest.setEntity(new UrlEncodedFormEntity(pairs));
        final HttpResponse postHttpResponse = NeatoHttpClient.execute(context, postHttpRequest);
        final int responseStatusCode = postHttpResponse.getStatusLine().getStatusCode();

        if (responseStatusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new UserUnauthorizedException(responseStatusCode, "Authorization Error");
        }
        responseEntity = postHttpResponse.getEntity();
        responseInputStream = responseEntity.getContent();

        if (responseStatusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
            // TODO: Handle the server error
            String response = AppUtils.convertStreamToString(responseInputStream);
            LogHelper.logD(TAG, String.format("Server ERROR for URL [%s] Response = %s", url, response));
            throw new NeatoServerException(responseStatusCode, response);
        }

        return responseInputStream;
    }

    public static String executeHttpPost(Context context, String methodName, Map<String, String> postParams)
            throws IOException, UserUnauthorizedException {
        InputStream is = executeHttpPostAndReturnStream(context, methodName, postParams);

        String response = AppUtils.convertStreamToString(is);
        return response;
    }

    private static String getUrlFromMethodName(Context context, String methodName) {

        String baseUrl = NeatoWebConstants.getBaseJsonUrl(context);
        LogHelper.logD(TAG, "Url Used:  " + baseUrl);
        String methodParamKey = NeatoWebConstants.QUERY_KEY_METHOD;
        String methodParamString = String.format("%s=%s", methodParamKey, encodeText(methodName));
        String url = baseUrl + "?" + methodParamString;
        return url;
    }

    private static final String encodeText(String text) {
        String encodedText = null;
        try {
            encodedText = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogHelper.log(TAG, "Encode text failed with exception", e);
        }

        return encodedText;
    }

}
