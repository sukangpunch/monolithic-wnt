package com.appcenter.monolithicwnt.user.presentation;

import com.appcenter.monolithicwnt.global.dto.response.SuccessResponse;
import com.appcenter.monolithicwnt.user.application.UserService;
import com.appcenter.monolithicwnt.user.dto.request.UserCreateRequest;
import com.appcenter.monolithicwnt.user.presentation.docs.UserControllerSpecification;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "사용자 관련 API 명세")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerSpecification {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<SuccessResponse<?>> createUser(@RequestBody UserCreateRequest request){
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.of(HttpStatus.CREATED, null));
    }
}
