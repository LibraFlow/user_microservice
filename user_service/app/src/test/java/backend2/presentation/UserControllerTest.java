package backend2.presentation;

import backend2.domain.Role;
import backend2.domain.UserDTO;
import backend2.business.usecase.user.*;
import backend2.business.usecase.auth.RegisterUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.junit.jupiter.api.AfterEach;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private GetAllUsersUseCase getAllUsersUseCase;

    @Mock
    private GetUserUseCase getUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @Mock
    private RightToBeForgottenUseCase rightToBeForgottenUseCase;

    @InjectMocks
    private UserController userController;

    private UserDTO testUserDTO;
    private Set<Role> testRoles;
    private Authentication authentication;
    private SecurityContext securityContext;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        // Initialize test roles
        testRoles = new HashSet<>();
        testRoles.add(Role.CUSTOMER);
        
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

    private void setupSecurityContext() {
        jwt = mock(Jwt.class);
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createUserTest() {
        // Act
        ResponseEntity<UserDTO> response = userController.createUser(testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(registerUserUseCase, times(1)).createUser(testUserDTO);
    }

    @Test
    void exerciseRightToBeForgottenTest() {
        // Arrange
        setupSecurityContext();
        when(jwt.getClaim("userId")).thenReturn(1);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        ResponseEntity<Void> response = userController.exerciseRightToBeForgotten(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rightToBeForgottenUseCase, times(1)).exerciseRightToBeForgotten(1);
    }

    @Test
    void getUserTest() {
        // Arrange
        setupSecurityContext();
        when(jwt.getClaim("userId")).thenReturn(1);
        when(authentication.getPrincipal()).thenReturn(jwt);
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(getUserUseCase.getUser(1)).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.getUser(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserDTO, response.getBody());
        verify(getUserUseCase, times(1)).getUser(1);
    }

    @Test
    void getAllUsersTest() {
        // Arrange
        setupSecurityContext();
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
        doReturn(authorities).when(authentication).getAuthorities();

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(getAllUsersUseCase, times(1)).getAllUsers();
    }

    @Test
    void updateUserTest() {
        // Arrange
        setupSecurityContext();
        when(jwt.getClaim("userId")).thenReturn(1);
        when(authentication.getPrincipal()).thenReturn(jwt);
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(updateUserUseCase.updateUser(1, testUserDTO, true)).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.updateUser(1, testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserDTO, response.getBody());
        verify(updateUserUseCase, times(1)).updateUser(1, testUserDTO, true);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
} 