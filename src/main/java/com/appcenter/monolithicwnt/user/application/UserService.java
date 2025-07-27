package com.appcenter.monolithicwnt.user.application;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.user.dto.request.UserCreateRequest;
import com.appcenter.monolithicwnt.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void createUser(UserCreateRequest request){
        validatePassword(request);
        validateEmail(request);
        validateNickname(request);
        userRepository.save(request.toUser());
    }

    private void validateNickname(UserCreateRequest request) {
        if(userRepository.existsByNickname(request.nickname())){
            throw new BusinessException(ErrorCode.NICKNAME_INVALID);
        }
    }

    private void validateEmail(UserCreateRequest request) {
        if(userRepository.existsByEmail(request.email())){
            throw new BusinessException(ErrorCode.EMAIL_INVALID);
        }
    }

    private void validatePassword(UserCreateRequest request) {
        if(!request.password().equals(request.rePassword())){
            throw new BusinessException(ErrorCode.PASSWORD_INVALID);
        }
    }

}
