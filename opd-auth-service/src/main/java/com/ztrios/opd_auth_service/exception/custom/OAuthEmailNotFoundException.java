package com.ztrios.opd_auth_service.exception.custom;


public class OAuthEmailNotFoundException extends RuntimeException {
    public OAuthEmailNotFoundException() {
        super("Email not found from OAuth2 provider");
    }
}

