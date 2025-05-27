package backend2.presentation;

import backend2.domain.UserDTO;
import backend2.domain.UserDataPortabilityDTO;
import backend2.business.usecase.user.RightToBeForgottenUseCase;
import backend2.business.usecase.user.GetAllUsersUseCase;
import backend2.business.usecase.user.GetUserUseCase;
import backend2.business.usecase.user.UpdateUserUseCase;
import backend2.business.usecase.user.GetUserDataPortabilityUseCase;
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
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Map;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final RightToBeForgottenUseCase rightToBeForgottenUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final GetUserDataPortabilityUseCase getUserDataPortabilityUseCase;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> exerciseRightToBeForgotten(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Integer userId = ((Number) jwt.getClaim("userId")).intValue();
        if (!userId.equals(id)) {
            return ResponseEntity.status(403).build();
        }
        rightToBeForgottenUseCase.exerciseRightToBeForgotten(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Integer userId = ((Number) jwt.getClaim("userId")).intValue();
        boolean isAdminOrLibrarian = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMINISTRATOR") || role.equals("ROLE_LIBRARIAN"));
        if (!isAdminOrLibrarian && !userId.equals(id)) {
            return ResponseEntity.status(403).build();
        }
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
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Integer userId = ((Number) jwt.getClaim("userId")).intValue();
        boolean isAdmin = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMINISTRATOR"));
        if (!userId.equals(id) && !isAdmin) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(updateUserUseCase.updateUser(id, userDto, isAdmin));
    }

    @GetMapping("/{id}/data-portability")
    public ResponseEntity<UserDataPortabilityDTO> getUserDataPortability(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Integer userId = ((Number) jwt.getClaim("userId")).intValue();
        if (!userId.equals(id)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(getUserDataPortabilityUseCase.getUserData(id));
    }
}