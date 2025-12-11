package com.ztrios.opd_auth_service.exception.custom;



public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {

        super(message);
    }
}
