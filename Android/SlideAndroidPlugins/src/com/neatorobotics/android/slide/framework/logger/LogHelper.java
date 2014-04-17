package com.neatorobotics.android.slide.framework.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class LogHelper {

    private static String TAG_PREFIX = "NEATO:";
    private static boolean sLogEnabled = true;
    private static boolean sEnableDebugLog = true;
    private static final String LOG_DATE_TIME_FORMAT = "[MM-dd-yyyy HH:mm:ss]";

    public static enum LogLevel {
        LOG_VERBOSE, LOG_DEBUG, LOG_INFO, LOG_WARN, LOG_ERROR
    }

    // Public helper method to enable the log
    // If this log is disabled then nothing is logged (using LogHelper class)
    // If sLogEnabled is enabled (by default enabled) then logging is done based
    // on the
    // log level enabled for the device. Log level can be changed using
    // adb shell setprop log.tag.<YOUR_LOG_TAG> <LEVEL>
    // by default log level is Log.INFO
    // If sEnableDebugLog is specified, then we log everything irrespective of
    // the level set on the
    // device
    public static void setLogEnabled(boolean enableLog) {
        sLogEnabled = enableLog;
    }

    // Public helper method to enable the debug log
    // If this log is disabled then all calls for logD will *not* be logged
    public static void setDebugLogEnabled(boolean enableLog) {
        sEnableDebugLog = enableLog;
    }

    // Public helper method to set the Tag prefix. By default this is "NEATO:"
    // Idea is, individual classes can have their own tags and we can analyze
    // the
    // logs based on that individual tags but to pick all the logs which are
    // application specified
    // we prefix with tag prefix. This will help to filter all logs which are
    // application specific
    public static void setTagPrefix(String prefix) {
        TAG_PREFIX = prefix;
    }

    // Private helper method to get the tag which will be used with Log.x APIs.
    // This simply
    // prefix the actual tag, passed by caller
    private static String getTag(String tag) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(LOG_DATE_TIME_FORMAT);
        Date now = new Date();
        String formattedDate = sdf.format(now);
        sb.append(formattedDate);
        sb.append(" ");
        sb.append(TAG_PREFIX);
        sb.append(" ");
        sb.append(tag);
        return sb.toString();
    }

    // Private helper method to determine, if we should log for the particular
    // level
    // If debug is enabled, then we log everything. Otherwise we log only based
    // on the
    // level set in the device using adb shell setprop log.tag.<YOUR_LOG_TAG>
    // <LEVEL>
    private static boolean isLoggable(String tag, int level) {
        if (sEnableDebugLog) {
            return true;
        }
        try {
            return Log.isLoggable(tag, level);
        } catch (Exception e) {

        }
        return (level >= Log.INFO) ? true : false;
    }

    // Public helper method to log the debug logs. This logging will be switched
    // off before shipping
    // If you want to log the information even in the shipped product, then call
    // "log" method
    public static final void logD(String tag, String message) {
        logD(tag, message, null);
    }

    // Public helper method to log the debug logs. This logging will be switched
    // off before shipping
    // If you want to log the information even in the shipped product, then call
    // "log" method
    public static final void logD(String tag, String message, Throwable e) {
        if (!sEnableDebugLog) {
            return;
        }

        LogLevel logLevel = (sEnableDebugLog) ? LogLevel.LOG_INFO : LogLevel.LOG_DEBUG;
        log(logLevel, tag, message, e);
    }

    // Public helper method to log information in logcat. This logging will
    // *not* be switched off before shipping
    // Use this method to log only the important information, which you want to
    // keep it even after the app ships
    public static final void log(String tag, String message) {
        log(tag, message, null);
    }

    // Public helper method to log information in logcat. This logging will
    // *not* be switched off before shipping
    // Use this method to log only the important information, which you want to
    // keep it even after the app ships
    public static final void log(String tag, String message, Throwable e) {
        LogLevel logLevel = (e != null) ? LogLevel.LOG_ERROR : LogLevel.LOG_INFO;
        log(logLevel, tag, message, e);
    }

    // Public helper method to log information in logcat. This logging will
    // *not* be switched off before shipping
    // Use this method to log only the important information, which you want to
    // keep it even after the app ships
    public static final void log(LogLevel level, String tag, String message, Throwable e) {
        if (!sLogEnabled) {
            return;
        }
        String appTag = getTag(tag);
        switch (level) {
            case LOG_INFO:
                if (isLoggable(tag, Log.INFO)) {
                    Log.i(appTag, message, e);
                }
                break;
            case LOG_WARN:
                if (isLoggable(tag, Log.WARN)) {
                    Log.w(appTag, message, e);
                }
                break;
            case LOG_ERROR:
                if (isLoggable(tag, Log.ERROR)) {
                    Log.e(appTag, message, e);
                }
                break;
            case LOG_DEBUG:
                if (isLoggable(tag, Log.DEBUG)) {
                    Log.d(appTag, message, e);
                }
                break;
            case LOG_VERBOSE:
                if (isLoggable(tag, Log.VERBOSE)) {
                    Log.v(appTag, message, e);
                }
                break;
            default:
                if (isLoggable(tag, Log.INFO)) {
                    Log.i(appTag, message, e);
                }
                break;
        }

    }
}
