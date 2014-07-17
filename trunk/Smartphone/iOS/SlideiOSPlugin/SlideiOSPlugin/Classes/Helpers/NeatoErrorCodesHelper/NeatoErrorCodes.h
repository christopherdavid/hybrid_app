#import <Foundation/Foundation.h>


/*************************SERVER ERROR CODES**********************/
/**
 * Authentication of the user failed.
 *  - This will occur when the email and/or the password is incorrect while Logging-in.
 *  - This will occur if the authentication token has expired.
 *  In this case the user will need to login again with the correct credentials.
 */
#define SERVER_ERROR_AUTHENTICATION_FAILED -101

/**
 * Email id entered does not match the usual format of the email id.
 *
 */
#define SERVER_ERROR_INVALID_EMAIL_ID -105

/**
 * Email id is already registered with the server.
 * - This will occur while creating a user account with already registered user email id.
 * The user needs to provide a email id which is not already registered.
 */
#define SERVER_ERROR_EMAIL_ALREADY_REGISTERED -106

/**
 * Create User failed due to some unknown server error.
 * - User should retry.
 */
#define SERVER_ERROR_CREATE_USER_FAILED_TRY_AGAIN -108


/**
 * Old password does not match to the current password of the user.
 * - To change the password, user needs to provide the current password. If entered password does not match
 * the current password, ERROR_OLD_PASSWORD_MISMATCH error is returned
 */
#define SERVER_ERROR_OLD_PASSWORD_MISMATCH -110

/**
 * Robot name value entered is empty
 * - This will occur when Robot name value is entered empty.
 * User should enter a non-empty for the robot name.
 */
#define SERVER_ERROR_INVALID_ROBOT_ACCOUNT_DETAIL -111

/**
 * Email id not found on the server.
 * - This will occur when the user sends a forgot password request and email id given isn't registered on the server.
 */
#define SERVER_ERROR_EMAIL_NOT_REGISTERED -112

/**
 * Robot id not found on the server.
 * - This will occur while associating a robot with the user.
 */
#define SERVER_ERROR_ROBOT_NOT_REGISTERED -114

/**
 * Alternate email id entered does not match the usual format of the email id.
 */
#define SERVER_ERROR_INVALID_ALTERNATE_EMAIL_ID -115

/**
 * Resend validation-email limit reached.
 * - This will occur when the validation email sending limit is reached.
 *   Currently the limit is 5.
 */
#define SERVER_ERROR_RESEND_VALIDATION_EMAIL_LIMIT_REACHED -116

/**
 * Email Id already validated.
 * - This will occur if the user requests to send a validation email, but the email is already validated on the server.
 */
#define SERVER_ERROR_EMAIL_ALREADY_VALIDATED -117

/**
 * Schedule version does not match. Please retrieve the latest schedule and then edit.
 * - This will occur if the user does not have the latest schedule and the user tries to edit it (Some other user edited
 * 	 the schedule persay).
 *   The application should request for the latest schedule so that the user can then edit the same.
 */
#define SERVER_ERROR_SCHEDULE_VERSION_MISMATCH -129

/**
 * Schedule type is not supported.
 * - This will occur if the application sends an invalid schedule type to the plugin layer.
 *   Currently supported schedule type is Basic schedule.
 */
#define SERVER_ERROR_INVALID_SCHEDULE_TYPE -133

/**
 * Invalid linking code.
 * - This will occur if the user enters an invalid linking code.
 */
#define SERVER_ERROR_INVALID_LINKING_CODE -154

/**
 * Linking code expired.
 * - This happens if the user hasn't entered the linking code before the timeout.
 */
#define SERVER_ERROR_LINKING_CODE_EXPIRED -155

/**
 * No schedule exists for given robot.
 * - This will occur if there is no schedule for the robot. The user/application should create a new schedule.
 */
#define SERVER_ERROR_NO_SCHEDULE_FOR_GIVEN_ROBOT -159

/**
 * Linking code already in use.
 * - This results if the linking code is already used for association.
 */
#define SERVER_ERROR_LINKING_CODE_IN_USE -180

/**
 * Association is already present for robot and user.
 */
#define SERVER_ERROR_ROBOT_USER_ASSOCIATION_ALREADY_EXISTS -182
/**
 * Robot already has some other user associated with it.
 */
#define SERVER_ERROR_ROBOT_HAS_ASSOCIATED_USER -192
#define SERVER_ERROR_TYPE_USER_UNAUTHORIZED 401


/*************************UI ERROR CODES**********************/
#define UI_ERROR_TYPE_UNKNOWN -501
#define UI_ERROR_NETWORK_ERROR -502
#define UI_JSON_PARSING_ERROR -503
#define UI_INVALID_SCHEDULE_ID -504
#define UI_INVALID_EVENT_ID -505
#define UI_ERROR_DB_ERROR -506
#define UI_JSON_CREATION_ERROR -507
#define UI_INVALID_PARAMETER -508
#define UI_ERROR_TYPE_USER_UNAUTHORIZED -509
#define UI_ERROR_NOT_SUPPORTED -510
#define UI_ROBOT_NOT_CONNECTED -511
#define UI_ROBOT_ALREADY_CONNECTED -512
#define UI_ROBOT_UNABLE_TO_CANCEL_INTEND_TO_DRIVE -513
#define UI_ROBOT_NO_DRIVE_REQUEST_FOUND -514
#define UI_DIFFERENT_ROBOT_ALREADY_CONNECTED -515
#define UI_ERROR_TYPE_WIFI_NOT_CONNECTED -516
#define UI_ERROR_TYPE_NO_INTERNET_CONNECTION -517
#define UI_ERROR_TYPE_NO_CLEANING_STATE_SET -518

/**
 * Authentication of the user failed.
 *  - This will occur when the email and/or the password is incorrect while Logging-in.
 *  - This will occur if the authentication token has expired.
 *  In this case the user will need to login again with the correct credentials.
 */
#define UI_ERROR_AUTHENTICATION_FAILED -101

/**
 * Email id entered does not match the usual format of the email id.
 *
 */
#define UI_ERROR_INVALID_EMAIL_ID -105

/**
 * Email id is already registered with the server.
 * - This will occur while creating a user account with already registered user email id.
 * The user needs to provide a email id which is not already registered.
 */
#define UI_ERROR_EMAIL_ALREADY_REGISTERED -106

/**
 * Create User failed due to some unknown server error.
 * - User should retry.
 */
#define UI_ERROR_CREATE_USER_FAILED_TRY_AGAIN -108


/**
 * Old password does not match to the current password of the user.
 * - To change the password, user needs to provide the current password. If entered password does not match
 * the current password, ERROR_OLD_PASSWORD_MISMATCH error is returned
 */
#define UI_ERROR_OLD_PASSWORD_MISMATCH -110

/**
 * Robot name value entered is empty
 * - This will occur when Robot name value is entered empty.
 * User should enter a non-empty for the robot name.
 */
#define UI_ERROR_INVALID_ROBOT_ACCOUNT_DETAIL -111

/**
 * Email id not found on the server.
 * - This will occur when the user sends a forgot password request and email id given isn't registered on the server.
 */
#define UI_ERROR_EMAIL_NOT_REGISTERED -112

/**
 * Robot id not found on the server.
 * - This will occur while associating a robot with the user.
 */
#define UI_ERROR_ROBOT_NOT_REGISTERED -114

/**
 * Alternate email id entered does not match the usual format of the email id.
 */
#define UI_ERROR_INVALID_ALTERNATE_EMAIL_ID -115

/**
 * Resend validation-email limit reached.
 * - This will occur when the validation email sending limit is reached.
 *   Currently the limit is 5.
 */
#define UI_ERROR_RESEND_VALIDATION_EMAIL_LIMIT_REACHED -116

/**
 * Email Id already validated.
 * - This will occur if the user requests to send a validation email, but the email is already validated on the server.
 */
#define UI_ERROR_EMAIL_ALREADY_VALIDATED -117

/**
 * Schedule version does not match. Please retrieve the latest schedule and then edit.
 * - This will occur if the user does not have the latest schedule and the user tries to edit it (Some other user edited
 * 	 the schedule persay).
 *   The application should request for the latest schedule so that the user can then edit the same.
 */
#define UI_ERROR_SCHEDULE_VERSION_MISMATCH -129

/**
 * Schedule type is not supported.
 * - This will occur if the application sends an invalid schedule type to the plugin layer.
 *   Currently supported schedule type is Basic schedule.
 */
#define UI_ERROR_INVALID_SCHEDULE_TYPE -133

/**
 * Invalid linking code.
 * - This will occur if the user enters an invalid linking code.
 */
#define UI_ERROR_INVALID_LINKING_CODE -154

/**
 * Linking code expired.
 * - This happens if the user hasn't entered the linking code before the timeout.
 */
#define UI_ERROR_LINKING_CODE_EXPIRED -155

/**
 * No schedule exists for given robot.
 * - This will occur if there is no schedule for the robot. The user/application should create a new schedule.
 */
#define UI_ERROR_NO_SCHEDULE_FOR_GIVEN_ROBOT -159

/**
 * Linking code already in use.
 * - This results if the linking code is already used for association.
 */
#define UI_ERROR_LINKING_CODE_IN_USE -180

/**
 * Association is already present for robot and user.
 */
#define UI_ERROR_ROBOT_USER_ASSOCIATION_ALREADY_EXISTS -182
/**
 * Robot already has some other user associated with it.
 */
#define UI_ERROR_ROBOT_HAS_ASSOCIATED_USER -192
#define UI_ERROR_TYPE_NETWORK_INFO_NOT_SET  -519


