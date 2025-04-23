package com.banku.userservice.service.oauth;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OAuthProviderService {
    private final Map<String, OAuthProvider> providers;

    public OAuthProviderService(List<OAuthProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(OAuthProvider::getProviderName, Function.identity()));
    }

    public OAuthProvider getProvider(String providerName) {
        OAuthProvider provider = providers.get(providerName.toLowerCase());
        if (provider == null) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + providerName);
        }
        return provider;
    }
} 