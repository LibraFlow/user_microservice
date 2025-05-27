package backend2.presentation;

import backend2.domain.UserDTO;
import backend2.business.usecase.auth.RegisterUserUseCase;
import backend2.business.usecase.user.GetUserUseCase;
import backend2.security.PasswordEncoderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.oauth2.jwt.Jwt;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import backend2.business.usecase.auth.CreateJwtTokenUseCase;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final PasswordEncoderService passwordEncoderService;
    private final CreateJwtTokenUseCase createJwtTokenUseCase;

    @PostMapping
    public ResponseEntity<UserDTO> c(@Valid @RequestBody UserDTO userDto) {
        return ResponseEntity.ok(registerUserUseCase.createUser(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO loginDto) {
        UserDTO user = getUserUseCase.getUserByUsernameIfNotDeleted(loginDto.getUsername());
        if (user == null || !passwordEncoderService.matches(loginDto.getPwd(), user.getPwd())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String jwt = createJwtTokenUseCase.createToken(user);
        ResponseCookie cookie = createJwtTokenUseCase.createJwtCookie(jwt);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(Map.of("token", jwt));
    }
} 