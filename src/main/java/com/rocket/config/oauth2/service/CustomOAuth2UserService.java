package com.rocket.config.oauth2.service;


import com.rocket.config.oauth2.entity.ProviderType;
import com.rocket.config.oauth2.entity.RoleType;
import com.rocket.config.oauth2.entity.UserPrincipal;
import com.rocket.config.oauth2.exception.OAuthProviderMissMatchException;
import com.rocket.config.oauth2.info.OAuth2UserInfo;
import com.rocket.config.oauth2.info.OAuth2UserInfoFactory;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        boolean exists = userRepository.existsByUuid(userInfo.getId());
        User savedUser;

        if (exists) {
            savedUser = userRepository.findByUuid(userInfo.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("can not found user"));
            if (savedUser != null) {
                if (providerType != savedUser.getProviderType()) {
                    throw new OAuthProviderMissMatchException(
                            "Looks like you're signed up with " + providerType +
                                    " account. Please use your " + savedUser.getProviderType() + " account to login."
                    );
                }
//                updateUser(savedUser, userInfo);
            }
        } else {
            savedUser = createUser(userInfo, providerType);
        }

        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {

        return userRepository.saveAndFlush(User.builder()
                .uuid(userInfo.getId())
                .username(userInfo.getName())
                .password(providerType + "-" + userInfo.getId())
                .email(userInfo.getEmail())
                .profileImage(userInfo.getImageUrl())
                .providerType(providerType)
                .roleType(RoleType.USER)
                .build());
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !user.getUsername().equals(userInfo.getName())) {
            user.updateUsername(userInfo.getName());
        }

        if (userInfo.getImageUrl() != null && user.getProfileImage() != null
                && !user.getProfileImage().equals(userInfo.getImageUrl())) {
            user.updateProfileImageUrl(userInfo.getImageUrl());
        }

        return user;
    }
}