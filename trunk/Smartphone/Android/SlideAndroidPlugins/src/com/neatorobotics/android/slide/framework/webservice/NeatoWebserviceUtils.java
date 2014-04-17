package com.neatorobotics.android.slide.framework.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.AppUtils;

public class NeatoWebserviceUtils {

    private static final String TAG = NeatoWebserviceUtils.class.getSimpleName();
    private static ObjectMapper resultMapper = new ObjectMapper();

    static {
        resultMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        resultMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        resultMapper.setSerializationInclusion(Include.NON_NULL);
    }

    public static <T extends NeatoWebserviceResult> T readValueHelper(NeatoHttpResponse response,
            Class<T> responseClassType) {
        T result = null;

        if (response.completed()) {
            try {
                LogHelper.logD(TAG, "Reading response");
                InputStream in = response.mResponseInputStream;
                String json = AppUtils.convertStreamToString(in);
                LogHelper.logD(TAG, "JSON = " + json);
                result = resultMapper.readValue(json, responseClassType);
            } catch (JsonParseException e) {
                LogHelper.log(TAG, "Exception in readValueHelper", e);

            } catch (JsonMappingException e) {
                LogHelper.log(TAG, "Exception in readValueHelper", e);

            } catch (IOException e) {
                LogHelper.log(TAG, "Exception in readValueHelper", e);
            } finally {
                if (result == null) {
                    result = createErrorResponseObject(responseClassType);
                }
            }
        } else {
            result = createErrorResponseObject(response, responseClassType);
        }
        return result;
    }

    public static <T extends NeatoWebserviceResult> T readValueHelper(String jsonResponse, Class<T> responseClassType) {
        T result = null;

        try {
            LogHelper.logD(TAG, "Reading response");
            LogHelper.logD(TAG, "JSON Response= " + jsonResponse);
            result = resultMapper.readValue(jsonResponse, responseClassType);
        } catch (JsonParseException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);

        } catch (JsonMappingException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);

        } catch (IOException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } finally {
            if (result == null) {
                result = createErrorResponseObject2(responseClassType);
            }
        }

        return result;
    }

    // Private helper method to create the XXXResult object
    private static <T> T createErrorResponseObject(NeatoHttpResponse response, Class<T> responseClassType) {
        T result = null;
        try {
            Class<?>[] cArgs = new Class[1];
            cArgs[0] = NeatoHttpResponse.class;
            result = responseClassType.getDeclaredConstructor(cArgs).newInstance(response);
        } catch (InstantiationException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (IllegalAccessException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (IllegalArgumentException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (SecurityException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (InvocationTargetException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (NoSuchMethodException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        }
        return result;
    }

    private static <T> T createErrorResponseObject(Class<T> responseClassType) {
        T result = null;
        try {
            Class<?>[] cArgs = new Class[3];
            cArgs[0] = int.class;
            cArgs[1] = int.class;
            cArgs[2] = String.class;
            responseClassType.getMethod("setResult", cArgs);
            result = responseClassType.getDeclaredConstructor(cArgs).newInstance(
                    NeatoWebConstants.RESPONSE_SERVER_ERROR, NeatoWebConstants.RESPONSE_SERVER_ERROR_JSON_PARSING,
                    "JSON Exception");
        } catch (InstantiationException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (IllegalAccessException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (IllegalArgumentException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (SecurityException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (InvocationTargetException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (NoSuchMethodException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        }
        return result;
    }

    // Private helper method to create the NeatoWebserviceResult and sets the
    // error code
    // This method is called when JSON parsing fails.
    private static <T extends NeatoWebserviceResult> T createErrorResponseObject2(Class<T> responseClassType) {
        T result = null;
        try {
            result = responseClassType.newInstance();
            result.setResult(NeatoWebConstants.RESPONSE_SERVER_ERROR,
                    NeatoWebConstants.RESPONSE_SERVER_ERROR_JSON_PARSING, "JSON Exception");
        } catch (InstantiationException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (IllegalAccessException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (IllegalArgumentException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        } catch (SecurityException e) {
            LogHelper.log(TAG, "Exception in readValueHelper", e);
        }
        return result;
    }

    // TODO: Use Object mapper from JSOn Utils.
    public static ObjectMapper getObjectMapper() {
        return resultMapper;
    }
}
