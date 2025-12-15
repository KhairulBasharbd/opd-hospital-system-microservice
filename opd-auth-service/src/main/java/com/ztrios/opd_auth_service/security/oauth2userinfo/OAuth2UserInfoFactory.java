package com.ztrios.opd_auth_service.security.oauth2userinfo;


import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getUserInfo(
            String registrationId,
            Map<String, Object> attributes) {

        if ("google".equalsIgnoreCase(registrationId)) {
            return new GoogleOAuth2UserInfo(attributes);
        }

        throw new IllegalArgumentException("Unsupported provider");
    }
}
