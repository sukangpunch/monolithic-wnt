package com.appcenter.monolithicwnt.user.presentation;

import com.appcenter.monolithicwnt.auth.presentation.AuthenticationPrincipal;
import com.appcenter.monolithicwnt.global.dto.response.SuccessResponse;
import com.appcenter.monolithicwnt.user.application.UserQueryService;
import com.appcenter.monolithicwnt.user.application.UserService;
import com.appcenter.monolithicwnt.user.dto.request.Authentication;
import com.appcenter.monolithicwnt.user.dto.request.UserCreateRequest;
import com.appcenter.monolithicwnt.user.dto.response.UserProfileResponse;
import com.appcenter.monolithicwnt.user.presentation.docs.UserControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {

    private final UserService userService;
    private final UserQueryService userQueryService;

    @PostMapping("/create")
    public ResponseEntity<SuccessResponse<?>> createUser(@RequestBody UserCreateRequest request){
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.of(HttpStatus.CREATED, null));
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<UserProfileResponse>> getUserProfile(
            @AuthenticationPrincipal Authentication authentication) {
        UserProfileResponse response = userQueryService.getUserProfile(authentication.id());
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(HttpStatus.OK, response));
    }
}
