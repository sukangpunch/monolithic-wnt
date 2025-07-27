package com.appcenter.monolithicwnt.auth.presentation;

import com.appcenter.monolithicwnt.auth.application.AuthService;
import com.appcenter.monolithicwnt.auth.dto.request.LoginRequest;
import com.appcenter.monolithicwnt.auth.presentation.docs.AuthControllerDocs;
import com.appcenter.monolithicwnt.global.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerDocs {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<Void>> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .sameSite("none")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(1800)
                .build();

        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(SuccessResponse.of(status, null));
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<Void>> logout() {
        ResponseCookie cookie = ResponseCookie.from("token", null)
                .sameSite("none")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        HttpStatus status = HttpStatus.OK;

        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(SuccessResponse.of(status, null));
    }
}
