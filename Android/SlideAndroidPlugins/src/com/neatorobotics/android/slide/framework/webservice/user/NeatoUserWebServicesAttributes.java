package com.neatorobotics.android.slide.framework.webservice.user;

public class NeatoUserWebServicesAttributes {

    public static final String ACCOUNT_TYPE_NATIVE = "Native";

    public static class CreateNeatoUser3 {
        public static final String METHOD_NAME = "user.create3";

        public static class Attribute {
            public static final String ACCOUNT_TYPE = "account_type";
            public static final String NAME = "name";
            public static final String EMAIL = "email";
            public static final String PASSWORD = "password";
            public static final String ALTERNATE_EMAIL = "alternate_email";
            public static final String EXTERNAL_SOCIAL_ID = "external_social_id";
            public static final String EXTRA_PARAM = "extra_param";
        }
    }

    public static class LoginNeatoUser {
        public static final String METHOD_NAME = "auth.get_user_auth_token";

        public static class Attribute {
            public static final String ACCOUNT_TYPE = "account_type";
            public static final String EMAIL = "email";
            public static final String PASSWORD = "password";
            public static final String EXTERNAL_SOCIAL_ID = "external_social_id";
        }

    }

    public static class GetNeatoUserDetails {
        public static final String METHOD_NAME = "user.get_user_account_details";

        public static class Attribute {
            public static final String EMAIL = "email";
            public static final String AUTHENTICATION_TOKEN = "auth_token";
        }
    }

    public static class GetUserAssociatedRobots {
        public static final String METHOD_NAME = "user.get_associated_robots";

        public static class Attribute {
            public static final String EMAIL = "email";
            public static final String AUTHENTICATION_TOKEN = "auth_token";
        }
    }

    public static class SetUserAttributes {
        public static final String METHOD_NAME = "user.set_attributes";

        public static class Attribute {
            public static final String AUTHENTICATION_TOKEN = "auth_token";
            public static final String PROFILE = "profile";
            public static final String ATTRIBUTE_NAME = "name";
            public static final String ATTRIBUTE_OPERATING_SYSTEM = "operating_system";
            public static final String ATTRIBUTE_VERSION = "version";
        }
    }

    public static class SetUserAccountDetails {
        public static final String METHOD_NAME = "user.set_account_details";

        public static class Attribute {
            public static final String EMAIL = "email";
            public static final String AUTHENTICATION_TOKEN = "auth_token";
            public static final String ATTRIBUTE_COUNTRYCODE = "country_code";
            public static final String ATTRIBUTE_OPTIN = "opt_in";
        }
    }

    public static class IsUserValidated {
        public static final String METHOD_NAME = "user.IsUserValidated";

        public static class Attribute {
            public static final String EMAIL = "email";
        }
    }

    public static class ResendValidationMail {
        public static final String METHOD_NAME = "user.ResendValidationEmail";

        public static class Attribute {
            public static final String EMAIL = "email";
        }
    }

    public static class RegisterPushNotifications {
        public static final String METHOD_NAME = "message.notification_registration";

        public static class Attribute {
            public static final String EMAIL = "user_email";
            public static final String REGISTRATION_ID = "registration_id";
            public static final String DEVICE_TYPE = "device_type";
        }
    }

    public static class UnregisterPushNotifications {
        public static final String METHOD_NAME = "message.notification_unregistration";

        public static class Attribute {
            public static final String REGISTRATION_ID = "registration_id";
        }
    }

    public static class ChangePassword {
        public static final String METHOD_NAME = "user.change_password";

        public static class Attribute {
            public static final String AUTHENTICATION_TOKEN = "auth_token";
            public static final String PASSWORD_OLD = "password_old";
            public static final String PASSWORD_NEW = "password_new";
        }
    }

    public static class ForgetPassword {
        public static final String METHOD_NAME = "user.forget_password";

        public static class Attribute {
            public static final String EMAIL = "email";
        }
    }

    public static class SetRobotPushNotificationOptions {
        public static final String METHOD_NAME = "message.set_user_push_notification_options";

        public static class Attribute {
            public static final String EMAIL = "email";
            public static final String JSON_OBJECT = "json_object";
        }
    }

    public static class GetRobotPushNotificationOptions {
        public static final String METHOD_NAME = "message.get_user_push_notification_options";

        public static class Attribute {
            public static final String EMAIL = "email";
        }
    }
}
