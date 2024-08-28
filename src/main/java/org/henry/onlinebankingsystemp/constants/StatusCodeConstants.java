package org.henry.onlinebankingsystemp.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StatusCodeConstants {

    /* Success Codes */
    public static final int SUCCESS = 10;  // Generic success
    public static final int OTP_SENT_SUCCESS = 11;  // OTP sent successfully
    public static final int PROFILE_UPDATED_SUCCESS = 12;  // Profile updated successfully
    public static final int TRANSACTION_SUCCESS = 13;  // Transaction completed successfully
    public static final int ONBOARDING_SUCCESS = 14;  // Onboarding completed successfully
    public static final int LOGIN_SUCCESS = 15;  // Login successful
    public static final int AUTH_TOKEN_CREATED_SUCCESS = 16; // Auth Token Created Successfully
    public static final int REFRESH_TOKEN_SUCCESS = 17; // Refresh Token generated successfully

    /* Onboarding Codes */
    public static final int ONBOARDING_FAILED = 20;  // Onboarding failed
    public static final int ONBOARDING_DUPLICATE_EMAIL = 21;  // Duplicate email during onboarding
    public static final int ONBOARDING_MISSING_INFORMATION = 22;  // Missing information during onboarding

    /* OTP-Related Codes  */
    public static final int OTP_EXPIRED = 30;  // OTP has expired
    public static final int OTP_INVALID = 31;  // OTP is invalid
    public static final int OTP_RESEND_LIMIT = 32;  // OTP resend limit exceeded
    public static final int OTP_VERIFICATION_FAILED = 33;  // OTP verification failed

    /* Login-Related Codes */
    public static final int LOGIN_FAILED = 40;  // Generic login failure
    public static final int LOGIN_INVALID_CREDENTIALS = 41;  // Invalid login credentials
    public static final int LOGIN_ACCOUNT_LOCKED = 42;  // Account locked due to multiple failed login attempts
    public static final int LOGIN_ACCOUNT_NOT_VERIFIED = 43;  // Account not verified during login

    /* Transaction-Related Codes */
    public static final int TRANSACTION_FAILED = 50;  // Generic transaction failure
    public static final int TRANSACTION_INSUFFICIENT_FUNDS = 51;  // Insufficient funds
    public static final int TRANSACTION_LIMIT_EXCEEDED = 52;  // Transaction limit exceeded
    public static final int TRANSACTION_INVALID_ACCOUNT = 53;  // Invalid account number

    /* Profile Update Codes */
    public static final int PROFILE_UPDATE_FAILED = 60;  // Generic profile update failure
    public static final int PROFILE_PICTURE_TOO_LARGE = 61;  // Profile picture size too large
    public static final int PROFILE_INVALID_EMAIL = 62;  // Invalid email format during profile update
    public static final int PROFILE_USERNAME_TAKEN = 63;  // Username already taken

    /* Exception and Error Codes */
    public static final int GENERIC_ERROR = 70;  // Generic error
    public static final int DATABASE_ERROR = 71;  // Database connection error
    public static final int AUTHENTICATION_FAILED = 72;  // Authentication failure
    public static final int ACCESS_DENIED = 73;  // Access denied
    public static final int VALIDATION_ERROR = 74;  // Data validation error
    public static final int SERVICE_UNAVAILABLE = 75;  // Service temporarily unavailable
    public static final int SIGNATURE_EXCEPTION = 76; // JWT Signature compromised
    public static final int EXPIRED_JWT_EXCEPTION = 77; // JWT Token expired
    public static final int RESOURCE_NOT_FOUND_EXCEPTION = 78; // Resource not found in the database
}
