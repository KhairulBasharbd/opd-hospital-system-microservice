package com.ztrios.opd_auth_service.security.oauth2;


import com.ztrios.opd_auth_service.entity.PatientProfileEntity;
import com.ztrios.opd_auth_service.entity.UserEntity;
import com.ztrios.opd_auth_service.enums.Role;
import com.ztrios.opd_auth_service.enums.Status;
import com.ztrios.opd_auth_service.exception.custom.OAuthEmailNotFoundException;
import com.ztrios.opd_auth_service.exception.custom.OAuthProviderNotSupportedException;
import com.ztrios.opd_auth_service.exception.custom.OAuthUserCreationException;
import com.ztrios.opd_auth_service.exception.custom.UserAccountInactiveException;
import com.ztrios.opd_auth_service.repository.PatientProfileRepository;
import com.ztrios.opd_auth_service.repository.UserRepository;
import com.ztrios.opd_auth_service.security.oauth2userinfo.OAuth2UserInfo;
import com.ztrios.opd_auth_service.security.oauth2userinfo.OAuth2UserInfoFactory;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;

    public CustomOAuth2UserService(UserRepository userRepository, PatientProfileRepository patientProfileRepository) {
        this.userRepository = userRepository;
        this.patientProfileRepository = patientProfileRepository;
    }

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(request);

        String provider = request
                .getClientRegistration()
                .getRegistrationId();

        OAuth2UserInfo userInfo;
        try {
            userInfo = OAuth2UserInfoFactory
                    .getUserInfo(provider, oAuth2User.getAttributes());
        } catch (Exception ex) {
            throw new OAuthProviderNotSupportedException(provider);
        }

        if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
            throw new OAuthEmailNotFoundException();
        }

        try {
            UserEntity user = userRepository
                    .findByEmail(userInfo.getEmail())
//                    .map(existingUser -> {
//                        if (existingUser.getStatus() != Status.ACTIVE) {
//                            throw new UserAccountInactiveException();
//                        }
//                        return existingUser;
//                    })
                    .orElseGet(() -> createNewOAuthUser(userInfo));

            return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority(user.getRole().toString())),
                    oAuth2User.getAttributes(),
                    "email"
            );

        } catch (RuntimeException ex) {
            throw ex; // rethrow custom exceptions
        } catch (Exception ex) {
            throw new OAuthUserCreationException("Failed to create OAuth user", ex);
        }
    }

    private UserEntity createNewOAuthUser(OAuth2UserInfo userInfo) {

        Instant now = Instant.now();

        UserEntity newUser = new UserEntity();
        newUser.setEmail(userInfo.getEmail());
        newUser.setFullName(userInfo.getName());
        newUser.setRole(Role.PATIENT);
        newUser.setStatus(Status.ACTIVE);
        newUser.setCreatedAt(now);
        newUser.setUpdatedAt(now);

        PatientProfileEntity patient = new PatientProfileEntity();
        patient.setUser(newUser);
        newUser.setPatientProfile(patient);

        UserEntity savedUser = userRepository.save(newUser);
        patientProfileRepository.save(patient);

        return savedUser;
    }


}
