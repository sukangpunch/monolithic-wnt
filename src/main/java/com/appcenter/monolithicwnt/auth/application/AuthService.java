package com.appcenter.monolithicwnt.auth.application;

import com.appcenter.monolithicwnt.auth.dto.request.LoginRequest;
import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.user.domain.User;
import com.appcenter.monolithicwnt.user.dto.request.Authentication;
import com.appcenter.monolithicwnt.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final TokenManager tokenManager;

    public String login(LoginRequest request) {
        User user =  userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(!user.checkPassword(request.password())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return tokenManager.createToken(user.getId(),user.getEmail());
    }

    public Authentication getAuthenticationByToken(String token) {
        Authentication authentication = tokenManager.extractAuthentication(token);
        log.info("Authentication: {}", authentication);

        if(!userRepository.existsById(authentication.id())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return authentication;
    }
}
