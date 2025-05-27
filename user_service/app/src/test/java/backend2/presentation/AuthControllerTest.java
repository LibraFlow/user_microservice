package backend2.presentation;

import backend2.domain.UserDTO;
import backend2.business.usecase.user.GetUserUseCase;
import backend2.security.PasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private GetUserUseCase getUserUseCase;
    @Mock
    private PasswordEncoderService passwordEncoderService;
    @InjectMocks
    private AuthController authController;

    private UserDTO testUserDTO;
    private Set<backend2.domain.Role> testRoles;

    @BeforeEach
    void setUp() {
        testRoles = new HashSet<>();
        testRoles.add(backend2.domain.Role.CUSTOMER);
        testUserDTO = UserDTO.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(testRoles)
                .build();
    }

    @Test
    void loginSuccessTest() {
        when(getUserUseCase.getUserByUsernameIfNotDeleted("testuser")).thenReturn(testUserDTO);
        when(passwordEncoderService.matches(anyString(), anyString())).thenReturn(true);
        ResponseEntity<?> response = authController.login(testUserDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("token"));
    }

    @Test
    void loginFailureTest() {
        when(getUserUseCase.getUserByUsernameIfNotDeleted("testuser")).thenReturn(null);
        ResponseEntity<?> response = authController.login(testUserDTO);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
} 