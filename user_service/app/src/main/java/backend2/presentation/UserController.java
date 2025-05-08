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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    @Value("${JWT_SECRET:12345678901234567890123456789012}")
    private String jwtSecret;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // subject from JWT
        // You need a way to get the user's ID from their username
        UserDTO user = getUserUseCase.getUserByUsername(username);
        if (user == null || !user.getId().equals(id)) {
            return ResponseEntity.status(403).build();
        }
        deleteUserUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(getUserUseCase.getUser(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdminOrLibrarian = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMINISTRATOR") || role.equals("ROLE_LIBRARIAN"));
        if (!isAdminOrLibrarian) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(getAllUsersUseCase.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody UserDTO userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDTO user = getUserUseCase.getUserByUsername(username);
        if (user == null || !user.getId().equals(id)) {
            return ResponseEntity.status(403).build();
        }
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
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        System.out.println("Secret bytes: " + Arrays.toString(jwtSecret.getBytes(StandardCharsets.UTF_8)));
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