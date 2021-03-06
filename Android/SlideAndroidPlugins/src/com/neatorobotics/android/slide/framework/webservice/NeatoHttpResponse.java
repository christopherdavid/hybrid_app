package com.neatorobotics.android.slide.framework.webservice;

import java.io.InputStream;

import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class NeatoHttpResponse {

    private static final String TAG = NeatoHttpResponse.class.getSimpleName();
    public int mResponse = 0;
    public int mServerErrorReason;
    public InputStream mResponseInputStream;

    public NeatoHttpResponse(int response, int responseStatusCode) {
        mResponse = response;
        mServerErrorReason = responseStatusCode;
    }

    public NeatoHttpResponse(InputStream responseInputStream) {
        mResponseInputStream = responseInputStream;
    }

    public NeatoHttpResponse(int response) {
        mResponse = response;
    }

    // response is successful
    public boolean completed() {
        LogHelper.log(TAG, "completed called. Response = " + mResponse);
        return (mResponse == NeatoWebConstants.RESPONSE_SUCCESS);
    }

    // response is not successful due to network error / IO error
    public final boolean networkError() {
        return (mResponse == NeatoWebConstants.RESPONSE_NETWORK_ERROR);
    }

    // Response is not successful as server error.
    public final boolean serverError() {
        return ((mResponse == NeatoWebConstants.RESPONSE_SERVER_ERROR_REASON_UNKNOWN) || (mResponse == NeatoWebConstants.RESPONSE_SERVER_ERROR));
    }

}
