package org.henry.onlinebankingsystemp.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StatusCodeConstants {

    /* Success Codes */
    public static final int SUCCESS = 200;  // Generic success
    public static final int OTP_SENT_SUCCESS = 201;  // OTP sent successfully
    public static final int PROFILE_UPDATED_SUCCESS = 202;  // Profile updated successfully
    public static final int TRANSACTION_SUCCESS = 203;  // Transaction completed successfully
    public static final int ONBOARDING_SUCCESS = 204;  // Onboarding completed successfully
    public static final int LOGIN_SUCCESS = 205;  // Login successful
    public static final int AUTH_TOKEN_CREATED_SUCCESS = 206; // Auth Token Created Successfully
    public static final int REFRESH_TOKEN_SUCCESS = 207;

    /* Onboarding Codes */
    public static final int ONBOARDING_FAILED = 330;  // Onboarding failed
    public static final int ONBOARDING_DUPLICATE_EMAIL = 331;  // Duplicate email during onboarding
    public static final int ONBOARDING_MISSING_INFORMATION = 332;  // Missing information during onboarding

    /* OTP-Related Codes  */
    public static final int OTP_EXPIRED = 300;  // OTP has expired
    public static final int OTP_INVALID = 301;  // OTP is invalid
    public static final int OTP_RESEND_LIMIT = 302;  // OTP resend limit exceeded
    public static final int OTP_VERIFICATION_FAILED = 303;  // OTP verification failed

    /* Login-Related Codes */
    public static final int LOGIN_FAILED = 340;  // Generic login failure
    public static final int LOGIN_INVALID_CREDENTIALS = 341;  // Invalid login credentials
    public static final int LOGIN_ACCOUNT_LOCKED = 342;  // Account locked due to multiple failed login attempts
    public static final int LOGIN_ACCOUNT_NOT_VERIFIED = 343;  // Account not verified during login

    /* Transaction-Related Codes */
    public static final int TRANSACTION_FAILED = 320;  // Generic transaction failure
    public static final int TRANSACTION_INSUFFICIENT_FUNDS = 321;  // Insufficient funds
    public static final int TRANSACTION_LIMIT_EXCEEDED = 322;  // Transaction limit exceeded
    public static final int TRANSACTION_INVALID_ACCOUNT = 323;  // Invalid account number

    /* Profile Update Codes */
    public static final int PROFILE_UPDATE_FAILED = 310;  // Generic profile update failure
    public static final int PROFILE_PICTURE_TOO_LARGE = 311;  // Profile picture size too large
    public static final int PROFILE_INVALID_EMAIL = 312;  // Invalid email format during profile update
    public static final int PROFILE_USERNAME_TAKEN = 313;  // Username already taken

    /* Exception and Error Codes */
    public static final int GENERIC_ERROR = 500;  // Generic error
    public static final int DATABASE_ERROR = 501;  // Database connection error
    public static final int AUTHENTICATION_FAILED = 502;  // Authentication failure
    public static final int ACCESS_DENIED = 503;  // Access denied
    public static final int VALIDATION_ERROR = 504;  // Data validation error
    public static final int SERVICE_UNAVAILABLE = 505;  // Service temporarily unavailable
    public static final int SIGNATURE_EXCEPTION = 506; // JWT Signature Compromised
    public static final int EXPIRED_JWT_EXCEPTION = 507; // JWT Token Expired
    public static final int RESOURCE_NOT_FOUND_EXCEPTION = 508; // Resource Not Found on the DATABASE
}
