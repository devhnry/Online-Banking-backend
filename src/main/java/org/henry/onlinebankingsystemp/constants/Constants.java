package org.henry.onlinebankingsystemp.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Constants {
    public static final String ONBOARDING_SUCCESS = "000";
    public static final String LOGIN_SUCCESS = "001";
    public static final String REFRESH_TOKEN_SUCCESS = "002";
    public static final String INVALID_PASSWORD = "003";

    // Exception codes
    public static final String SIGNATURE_EXCEPTION = "100";
    public static final String EXPIRED_JWT_EXCEPTION = "101";
    public static final String RUNTIME_EXCEPTION = "102";
    public static final String METHOD_INVALID_EXCEPTION = "103";
    public static final String RESOURCE_NOT_FOUND_EXCEPTION = "104";

    // Customer-related codes
    public static final String CUSTOMER_ALREADY_EXISTS = "200";
    public static final String GET_BALANCE_SUCCESS = "201";
    public static final String GET_DETAILS_SUCCESS = "202";
    public static final String DEPOSIT_NOT_ENOUGH = "203";

    // Token-related codes
    public static final String AUTH_TOKEN_CREATED = "204";
    public static final String AUTH_TOKEN_BAD_REQUEST = "205";
}
