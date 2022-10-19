package com.rocket.config.oauth2.handler;

import com.rocket.auth.entitiy.RedisAuthToken;
import com.rocket.config.oauth2.repository.RedisAuthTokenRepository;
import com.rocket.config.jwt.AuthToken;
import com.rocket.config.jwt.AuthTokenProvider;
import com.rocket.config.oauth2.entity.ProviderType;
import com.rocket.config.oauth2.entity.RoleType;
import com.rocket.config.oauth2.info.OAuth2UserInfo;
import com.rocket.config.oauth2.info.OAuth2UserInfoFactory;
import com.rocket.config.oauth2.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.rocket.config.properties.AppProperties;
import com.rocket.error.exception.UserException;
import com.rocket.user.user.entity.UserRefreshToken;
import com.rocket.user.user.repository.UserRefreshTokenRepository;
import com.rocket.utils.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static com.rocket.config.oauth2.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.rocket.error.type.UserErrorCode.USER_NOT_FOUND;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private final AuthTokenProvider tokenProvider;
        private final AppProperties appProperties;
        private final UserRefreshTokenRepository userRefreshTokenRepository;
        private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

        @Override
        @Transactional
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            String targetUrl = determineTargetUrl(request, response, authentication);

            if (response.isCommitted()) {
                logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
                return;
            }

            clearAuthenticationAttributes(request, response);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }

        protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
            Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                    .map(Cookie::getValue);

            logger.error("redirectUri : " + redirectUri);
            logger.error("redirectUri : " + redirectUri.get());
            if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
                throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
            }

            String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

            OidcUser user = ((OidcUser) authentication.getPrincipal());
            OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
            Collection<? extends GrantedAuthority> authorities = ((OidcUser) authentication.getPrincipal()).getAuthorities();

            RoleType roleType = hasAuthority(authorities, RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;

            Date now = new Date();
            AuthToken accessToken = tokenProvider.createAuthToken(
                    userInfo.getId(),
                    roleType.getCode(),
                    new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
            );

            // refresh 토큰 설정
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            AuthToken refreshToken = tokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),
                    new Date(now.getTime() + refreshTokenExpiry)
            );

            // DB 저장
            UserRefreshToken userRefreshToken;
            boolean exists = userRefreshTokenRepository.existsByUuid(userInfo.getId());

            if (exists) {
                userRefreshToken = userRefreshTokenRepository.findByUuid(userInfo.getId())
                        .orElseThrow(() -> new UserException(USER_NOT_FOUND));

                userRefreshToken.updateRefreshToken(refreshToken.getToken());
            } else {
                userRefreshToken =  UserRefreshToken.builder()
                        .uuid(userInfo.getId())
                        .refreshToken(refreshToken.getToken())
                        .build();
                userRefreshTokenRepository.saveAndFlush(userRefreshToken);
            }

//            RedisAuthToken redisAuthToken = RedisAuthToken.builder()
//                    .uuid(userInfo.getId())
//                    .token(accessToken.getToken())
//                    .build();
//
//            authTokenRepository.save(redisAuthToken);

//            int cookieMaxAge = (int) refreshTokenExpiry / 60;
//            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
//            CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("token", accessToken.getToken())
                    .build().toUriString();
        }

        protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
            super.clearAuthenticationAttributes(request);
            authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        }

        private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
            if (authorities == null) {
                return false;
            }

            for (GrantedAuthority grantedAuthority : authorities) {
                if (authority.equals(grantedAuthority.getAuthority())) {
                    return true;
                }
            }
            return false;
        }

        private boolean isAuthorizedRedirectUri(String uri) {
            URI clientRedirectUri = URI.create(uri);
            logger.error("isAuthorizedRedirectUri uri : " + uri);
            logger.error("clientRedirectUri : " + clientRedirectUri.getHost());

            return appProperties.getOauth2().getAuthorizedRedirectUris()
                    .stream()
                    .anyMatch(authorizedRedirectUri -> {
                        URI authorizedURI = URI.create(authorizedRedirectUri);
                        logger.error("authorizedURI : " + authorizedURI.getHost());
                        if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                                && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                            return true;
                        }
                        return false;
                    });
        }
}
