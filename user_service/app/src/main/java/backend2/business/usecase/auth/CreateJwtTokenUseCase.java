package backend2.business.usecase.auth;

import backend2.domain.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class CreateJwtTokenUseCase {
    @Value("${JWT_SECRET:12345678901234567890123456789012}")
    private String jwtSecret;

    private final Environment environment;

    public CreateJwtTokenUseCase(Environment environment) {
        this.environment = environment;
    }

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
        boolean isProd = environment.getActiveProfiles().length > 0 && 
                        environment.getActiveProfiles()[0].equals("prod");
        
        return ResponseCookie.from("jwt", jwt)
            .httpOnly(true)
            .secure(isProd)
            .path("/")
            .sameSite("Lax")
            .maxAge(3600)
            .build();
    }
} 