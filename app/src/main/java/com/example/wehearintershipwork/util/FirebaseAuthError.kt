package com.example.wehearintershipwork.util

/**
 * error codes of different error throws by firebase during authentication
 * @param errorMsg is the message to display when that particular error occurs
 */
enum class FirebaseAuthError(val errorMsg: String) {




    ERROR_REQUIRES_RECENT_LOGIN("This operation is sensitive and requires recent authentication. Log in again before retrying this request."),

    ERROR_EMAIL_ALREADY_IN_USE("The email address is already in use by another account."),

    ERROR_TOO_MANY_REQUESTS("We have blocked all requests from this device due to unusual activity. Try again later."),

    ERROR_INVALID_PHONE_NUMBER("Invalid phone number"),

    ERROR_INVALID_VERIFICATION_CODE("Invalid verification code."),

    ERROR_SESSION_EXPIRED("The verification code has expired. Please re-send the verification code to try again."),

    ERROR_QUOTA_EXCEEDED("The sms quota for this project has been exceeded. Try again later"),

    ERROR_UNKNOWN("An unknown error occurred.")
}