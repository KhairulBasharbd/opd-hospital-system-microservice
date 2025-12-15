package com.ztrios.opd_auth_service.security.oauth2userinfo;

public interface OAuth2UserInfo {
    String getEmail();
    String getName();
    String getProviderId();
}
