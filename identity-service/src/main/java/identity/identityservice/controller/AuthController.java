package identity.identityservice.controller;

import identity.identityservice.dto.RefreshTokenRequest;
import identity.identityservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

}
