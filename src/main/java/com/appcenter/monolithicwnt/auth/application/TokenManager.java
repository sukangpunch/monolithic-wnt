package com.appcenter.monolithicwnt.auth.application;

import com.appcenter.monolithicwnt.user.dto.request.Authentication;
import io.jsonwebtoken.Claims;

public interface TokenManager {
    String createToken(Long id, String email);

    Claims extractClaims(String token);

    Authentication extractAuthentication(String token);
}
