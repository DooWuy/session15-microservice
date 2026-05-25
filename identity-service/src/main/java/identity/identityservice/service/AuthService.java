package identity.identityservice.service;

import identity.identityservice.dto.RefreshTokenRequest;
import identity.identityservice.entity.RefreshToken;
import identity.identityservice.repository.RefreshTokenRepository;
import identity.identityservice.security.JwtProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AuthService {


    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản hoặc mật khẩu không chính xác"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không chính xác");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getUsername(), user.getRole());

        String refreshTokenStr = jwtProvider.generateRefreshTokenString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .username(user.getUsername())
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        refreshTokenRepository.save(refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .build();
    }
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String oldTokenStr = request.getRefreshToken();

        RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(oldTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh Token không tồn tại hoặc không hợp lệ"));

        if (oldRefreshToken.isExpired()) {
            refreshTokenRepository.delete(oldRefreshToken); // Xóa token hết hạn khỏi DB
            throw new RuntimeException("Refresh Token đã hết hạn. Vui lòng đăng nhập lại");
        }


        User user = userRepository.findByUsername(oldRefreshToken.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại trên hệ thống"));

        refreshTokenRepository.deleteByToken(oldTokenStr);

        String newAccessToken = jwtProvider.generateAccessToken(user.getUsername(), user.getRole());
        String newRefreshTokenStr = jwtProvider.generateRefreshTokenString();

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefreshTokenStr)
                .username(user.getUsername())
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        refreshTokenRepository.save(newRefreshToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenStr)
                .build();
    }
}
