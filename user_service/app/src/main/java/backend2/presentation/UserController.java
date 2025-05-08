package backend2.presentation;

import backend2.domain.UserDTO;
import backend2.business.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Set;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AddUserUseCase addUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDto) {
        return ResponseEntity.ok(addUserUseCase.createUser(userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        deleteUserUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(getUserUseCase.getUser(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(getAllUsersUseCase.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody UserDTO userDto) {
        return ResponseEntity.ok(updateUserUseCase.updateUser(id, userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO loginDto) {
        // Authenticate user (pseudo, replace with real check)
        UserDTO user = getAllUsersUseCase.getAllUsers().stream()
            .filter(u -> u.getUsername().equals(loginDto.getUsername()) && String.valueOf(u.getPwd()).equals(String.valueOf(loginDto.getPwd())))
            .findFirst().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.length() < 32) {
            secret = "12345678901234567890123456789012";
        }
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        System.out.println("Secret bytes: " + Arrays.toString(secret.getBytes(StandardCharsets.UTF_8)));
        long now = System.currentTimeMillis();
        String jwt = Jwts.builder()
            .setSubject(user.getUsername())
            .claim("roles", user.getRoles())
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + 3600_000)) // 1 hour
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
        System.out.println("JWT issued: " + jwt);
        return ResponseEntity.ok().body("Bearer " + jwt);
    }
}