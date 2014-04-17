package com.neatorobotics.android.slide.framework.pluginhelper;

public class ErrorTypes {

    public static final int ERROR_TYPE_UNKNOWN = -501;
    public static final int ERROR_NETWORK_ERROR = -502;
    public static final int JSON_PARSING_ERROR = -503;
    public static final int INVALID_SCHEDULE_ID = -504;
    public static final int INVALID_EVENT_ID = -505;
    public static final int ERROR_DB_ERROR = -506;
    public static final int JSON_CREATION_ERROR = -507;
    public static final int INVALID_PARAMETER = -508;
    public static final int ERROR_TYPE_USER_UNAUTHORIZED = -509;
    public static final int ERROR_NOT_SUPPORTED = -510;
    public static final int ROBOT_NOT_CONNECTED = -511;
    public static final int ROBOT_ALREADY_CONNECTED = -512;
    public static final int ROBOT_UNABLE_TO_CANCEL_INTEND_TO_DRIVE = -513;
    public static final int ROBOT_NO_DRIVE_REQUEST_FOUND = -514;
    public static final int DIFFERENT_ROBOT_ALREADY_CONNECTED = -515;

    public static final int ERROR_TYPE_WIFI_NOT_CONNECTED = -516;
    public static final int ERROR_TYPE_NO_INTERNET_CONNECTION = -517;

    /**
     * Authentication of the user failed. - This will occur when the email
     * and/or the password is incorrect while Logging-in. - This will occur if
     * the authentication token has expired. In this case the user will need to
     * login again with the correct credentials.
     */
    public static final int ERROR_AUTHENTICATION_FAILED = -101;

    /**
     * Email id entered does not match the usual format of the email id.
     * 
     */
    public static final int ERROR_INVALID_EMAIL_ID = -105;

    /**
     * Email id is already registered with the server. - This will occur while
     * creating a user account with already registered user email id. The user
     * needs to provide a email id which is not already registered.
     */
    public static final int ERROR_EMAIL_ALREADY_REGISTERED = -106;

    /**
     * Create User failed due to some unknown server error. - User should retry.
     */
    public static final int ERROR_CREATE_USER_FAILED_TRY_AGAIN = -108;

    /**
     * Old password does not match to the current password of the user. - To
     * change the password, user needs to provide the current password. If
     * entered password does not match the current password,
     * ERROR_OLD_PASSWORD_MISMATCH error is returned
     */
    public static final int ERROR_OLD_PASSWORD_MISMATCH = -110;

    /**
     * Robot name value entered is empty - This will occur when Robot name value
     * is entered empty. User should enter a non-empty for the robot name.
     */
    public static final int ERROR_INVALID_ROBOT_ACCOUNT_DETAIL = -111;

    /**
     * Email id not found on the server. - This will occur when the user sends a
     * forgot password request and email id given isn't registered on the
     * server.
     */
    public static final int ERROR_EMAIL_NOT_REGISTERED = -112;

    /**
     * Robot id not found on the server. - This will occur while associating a
     * robot with the user.
     */
    public static final int ERROR_ROBOT_NOT_REGISTERED = -114;

    /**
     * Alternate email id entered does not match the usual format of the email
     * id.
     */
    public static final int ERROR_INVALID_ALTERNATE_EMAIL_ID = -115;

    /**
     * Resend validation-email limit reached. - This will occur when the
     * validation email sending limit is reached. Currently the limit is 5.
     */
    public static final int ERROR_RESEND_VALIDATION_EMAIL_LIMIT_REACHED = -116;

    /**
     * Email Id already validated. - This will occur if the user requests to
     * send a validation email, but the email is already validated on the
     * server.
     */
    public static final int ERROR_EMAIL_ALREADY_VALIDATED = -117;

    /**
     * Schedule version does not match. Please retrieve the latest schedule and
     * then edit. - This will occur if the user does not have the latest
     * schedule and the user tries to edit it (Some other user edited the
     * schedule persay). The application should request for the latest schedule
     * so that the user can then edit the same.
     */
    public static final int ERROR_SCHEDULE_VERSION_MISMATCH = -129;

    /**
     * Schedule type is not supported. - This will occur if the application
     * sends an invalid schedule type to the plugin layer. Currently supported
     * schedule type is Basic schedule.
     */
    public static final int ERROR_INVALID_SCHEDULE_TYPE = -133;

    /**
     * Invalid linking code. - This will occur if the user enters an invalid
     * linking code.
     */
    public static final int ERROR_INVALID_LINKING_CODE = -154;

    /**
     * Linking code expired. - This happens if the user hasn't entered the
     * linking code before the timeout.
     */
    public static final int ERROR_LINKING_CODE_EXPIRED = -155;

    /**
     * No schedule exists for given robot. - This will occur if there is no
     * schedule for the robot. The user/application should create a new
     * schedule.
     */
    public static final int ERROR_NO_SCHEDULE_FOR_GIVEN_ROBOT = -159;

    /**
     * Linking code already in use. - This results if the linking code is
     * already used for association.
     */
    public static final int ERROR_LINKING_CODE_IN_USE = -180;

    /**
     * Association is already present for robot and user
     */
    public static final int ERROR_ROBOT_USER_ASSOCIATION_ALREADY_EXISTS = -182;

    /**
     * Robot already has some other user associated with it.
     */
    public static final int ERROR_ROBOT_HAS_ASSOCIATED_USER = -192;

}