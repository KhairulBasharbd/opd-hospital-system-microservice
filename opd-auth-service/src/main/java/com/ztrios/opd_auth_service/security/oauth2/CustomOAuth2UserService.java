package com.ztrios.opd_auth_service.security.oauth2;


import com.ztrios.opd_auth_service.entity.UserEntity;
import com.ztrios.opd_auth_service.enums.Role;
import com.ztrios.opd_auth_service.repository.UserRepository;
import com.ztrios.opd_auth_service.security.oauth2userinfo.OAuth2UserInfo;
import com.ztrios.opd_auth_service.security.oauth2userinfo.OAuth2UserInfoFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(request);

        String provider =
                request.getClientRegistration().getRegistrationId();

        OAuth2UserInfo userInfo =
                OAuth2UserInfoFactory.getUserInfo(provider, oAuth2User.getAttributes());

        // 1️⃣ Find or create local user
        UserEntity user = userRepository
                .findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(userInfo.getEmail());
                    newUser.setFullName(userInfo.getName());
                    newUser.setRole(Role.PATIENT);

                    //newUser.setProvider(AuthProvider.GOOGLE);
                    return userRepository.save(newUser);
                });

        // 2️⃣ Return Spring Security principal
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRole().toString())),
                oAuth2User.getAttributes(),
                "email");
    }
}
