package backend2.business.usecase.auth;

import backend2.domain.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class CreateJwtTokenUseCase {
    @Value("${JWT_SECRET:12345678901234567890123456789012}")
    private String jwtSecret;

    public String createToken(UserDTO user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .claim("roles", user.getRoles())
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + 3600_000))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public ResponseCookie createJwtCookie(String jwt) {
        return ResponseCookie.from("jwt", jwt)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Lax")
            .maxAge(3600)
            .build();
    }
} 