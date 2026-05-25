package identity.identityservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
@Component
public class JwtProvider {


    @Value("${app.jwt.secret:YourSuperSecretKeyForJWTGenerationThatIsVeryLongAndSecure}")
    private String jwtSecret;

    public String generateAccessToken(String username, String role) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(15, ChronoUnit.MINUTES);

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("jti", UUID.randomUUID().toString()) // Bắt buộc có JWT ID duy nhất
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshTokenString() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }

}
