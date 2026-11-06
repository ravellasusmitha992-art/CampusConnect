package com.campusconnect.config;

import com.campusconnect.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Thin wrapper so SecurityConfig can depend on the UserDetailsService
 * without a circular bean dependency on AuthenticationManager.
 */
@Component
@RequiredArgsConstructor
public class CustomUserDetailsServiceHolder {
    private final CustomUserDetailsService service;

    public CustomUserDetailsService getService() {
        return service;
    }
}
