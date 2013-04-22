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

import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class NeatoWebserviceHelper {

	private static final String TAG = NeatoWebserviceHelper.class.getSimpleName();

	public static NeatoHttpResponse executeHttpPost(Context context, String methodName, Map<String, String> postParams) {
		NeatoHttpResponse result = null;
		String url = getUrlFromMethodName(methodName);
		LogHelper.logD(TAG, "Executing URL = " + url);
		postParams.put(NeatoWebConstants.QUERY_KEY_APIKEY, NeatoWebConstants.getApiKey());
		HttpPost postHttpRequest = new HttpPost(url);
		
		final List<NameValuePair> pairs = new ArrayList<NameValuePair>();	

		for (Entry<String, String> params : postParams.entrySet()) {
			pairs.add(new BasicNameValuePair(params.getKey(), params.getValue()));
			LogHelper.logD(TAG, "Key = " + params.getKey() + " Value = " + params.getValue());
		}

		HttpEntity responseEntity = null;
		InputStream responseInputStream = null;


		try {
			postHttpRequest.setEntity(new UrlEncodedFormEntity(pairs));
			final HttpResponse postHttpResponse = NeatoHttpClient.execute(context, postHttpRequest);
			final int responseStatusCode = postHttpResponse.getStatusLine().getStatusCode();

			switch(responseStatusCode) {
			case HttpStatus.SC_OK:

				responseEntity = postHttpResponse.getEntity();
				responseInputStream = responseEntity.getContent();
				result = new NeatoHttpResponse(responseInputStream);
				
				break;
			case HttpStatus.SC_UNAUTHORIZED:
				result = new NeatoHttpResponse(NeatoWebConstants.RESPONSE_SERVER_ERROR, responseStatusCode);
				break;
			default:	
				result = new NeatoHttpResponse(NeatoWebConstants.RESPONSE_SERVER_ERROR, responseStatusCode);
				break;
			}	    

		} catch (UnsupportedEncodingException e) {
			LogHelper.log(TAG, "UnsupportedEncodingException in doing HTTP Post request", e);
			result = new NeatoHttpResponse(NeatoWebConstants.RESPONSE_SERVER_ERROR, NeatoWebConstants.RESPONSE_SERVER_ERROR_REASON_UNKNOWN);
		}
		catch (IllegalStateException e) {
			LogHelper.log(TAG, "IllegalStateException in doing HTTP Post request", e);
			result = new NeatoHttpResponse(NeatoWebConstants.RESPONSE_SERVER_ERROR, NeatoWebConstants.RESPONSE_SERVER_ERROR_REASON_UNKNOWN);

		} catch (IOException e) {
			LogHelper.log(TAG, "IOException in doing HTTP Post request", e);
			result = new NeatoHttpResponse(NeatoWebConstants.RESPONSE_NETWORK_ERROR);
		}
		return result;
	}
	
	public static String getUrlFromMethodName(String methodName) {

		String baseUrl = NeatoWebConstants.getBaseJsonUrl();
		LogHelper.logD(TAG, "Url Used:  " + baseUrl);
		String methodParamKey = NeatoWebConstants.QUERY_KEY_METHOD;
		String methodParamString = String.format("%s=%s", methodParamKey, encodeText(methodName));
		String url = baseUrl+ "?" + methodParamString;
		return url;
	}

	private static final String encodeText(String text) {
		String encodedText = null;
		try {
			encodedText = URLEncoder.encode(text, "UTF-8");
		} 
		catch (UnsupportedEncodingException e) {
			LogHelper.log(TAG, "Encode text failed with exception" , e);
		}

		return encodedText;
	}	

}
