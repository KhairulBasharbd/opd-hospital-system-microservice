package com.ztrios.opd_auth_service.exception.custom;

public class OAuthProviderNotSupportedException extends RuntimeException {
    public OAuthProviderNotSupportedException(String provider) {
        super("OAuth provider not supported: " + provider);
    }
}
