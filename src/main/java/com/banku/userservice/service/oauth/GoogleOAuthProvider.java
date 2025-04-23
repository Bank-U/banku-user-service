package com.banku.userservice.service.oauth;

import com.banku.userservice.aggregate.UserAggregate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GoogleOAuthProvider implements OAuthProvider {
    private static final String PROVIDER_NAME = "google";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public UserAggregate getUserInfo(String code) {
        String accessToken = getAccessToken(code);
        return getUserInfoFromGoogle(accessToken);
    }

    @Override
    public String getAuthorizationUrl() {
        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&scope=email profile",
                AUTH_URL, clientId, redirectUri);
    }

    private String getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    private UserAggregate getUserInfoFromGoogle(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, request, Map.class);

        Map<String, Object> userInfo = response.getBody();
        return UserAggregate.builder()
                .email((String) userInfo.get("email"))
                .firstName((String) userInfo.get("given_name"))
                .lastName((String) userInfo.get("family_name"))
                .profilePicture((String) userInfo.get("picture"))
                .provider(PROVIDER_NAME)
                .providerId((String) userInfo.get("id"))
                .build();
    }
} 