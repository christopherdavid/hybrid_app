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
import com.neatorobotics.android.slide.framework.utils.AppUtils;

public class MobileWebServiceClient {

	private static final String TAG = MobileWebServiceClient.class.getSimpleName();

	private static InputStream executeHttpPostAndReturnStream(Context context, String methodName, Map<String, String> postParams) throws IOException, UserUnauthorizedException {
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
	
	public static String executeHttpPost(Context context, String methodName, Map<String, String> postParams) throws IOException, UserUnauthorizedException {
		InputStream is = executeHttpPostAndReturnStream(context, methodName, postParams);
		
		String response = AppUtils.convertStreamToString(is);
		return response;
	}
	

	private static String getUrlFromMethodName(String methodName) {

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