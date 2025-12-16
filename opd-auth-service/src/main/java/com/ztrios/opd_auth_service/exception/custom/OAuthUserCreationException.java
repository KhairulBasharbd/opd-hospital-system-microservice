package com.ztrios.opd_auth_service.exception.custom;

public class OAuthUserCreationException extends RuntimeException {
    public OAuthUserCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

