package backend2.presentation;

import backend2.domain.UserDTO;
import backend2.business.usecase.user.GetUserUseCase;
import backend2.business.usecase.auth.CreateJwtTokenUseCase;
import backend2.security.PasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;

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
    @Mock
    private CreateJwtTokenUseCase createJwtTokenUseCase;
    @InjectMocks
    private AuthController authController;

    private UserDTO testUserDTO;
    private Set<backend2.domain.Role> testRoles;
    private String testJwtToken;
    private ResponseCookie testCookie;

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
        testJwtToken = "test.jwt.token";
        testCookie = ResponseCookie.from("jwt", testJwtToken).build();
    }

    @Test
    void loginSuccessTest() {
        when(getUserUseCase.getUserByUsernameIfNotDeleted("testuser")).thenReturn(testUserDTO);
        when(passwordEncoderService.matches(anyString(), anyString())).thenReturn(true);
        when(createJwtTokenUseCase.createToken(testUserDTO)).thenReturn(testJwtToken);
        when(createJwtTokenUseCase.createJwtCookie(testJwtToken)).thenReturn(testCookie);

        ResponseEntity<?> response = authController.login(testUserDTO);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("token"));
        assertEquals(testJwtToken, ((Map<?, ?>) response.getBody()).get("token"));
        assertEquals(testCookie.toString(), response.getHeaders().getFirst("Set-Cookie"));
    }

    @Test
    void loginFailureTest() {
        when(getUserUseCase.getUserByUsernameIfNotDeleted("testuser")).thenReturn(null);
        ResponseEntity<?> response = authController.login(testUserDTO);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
} 