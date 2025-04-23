package com.banku.userservice.service.oauth;

import com.banku.userservice.aggregate.UserAggregate;

public interface OAuthProvider {
    String getProviderName();
    UserAggregate getUserInfo(String code);
    String getAuthorizationUrl();
} 