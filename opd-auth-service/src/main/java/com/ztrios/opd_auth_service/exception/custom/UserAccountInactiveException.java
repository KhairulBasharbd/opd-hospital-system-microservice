package com.ztrios.opd_auth_service.exception.custom;


public class UserAccountInactiveException extends RuntimeException {
    public UserAccountInactiveException() {
        super("User account is inactive or blocked");
    }
}

