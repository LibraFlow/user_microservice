package backend2.presentation;

import backend2.domain.Role;
import backend2.domain.UserDTO;
import backend2.business.user.*;
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
    private AddUserUseCase addUserUseCase;

    @Mock
    private DeleteUserUseCase deleteUserUseCase;

    @Mock
    private GetAllUsersUseCase getAllUsersUseCase;

    @Mock
    private GetUserUseCase getUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @InjectMocks
    private UserController userController;

    private UserDTO testUserDTO;
    private Set<Role> testRoles;

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

    @Test
    void createUserTest() {
        // Arrange
        when(addUserUseCase.createUser(any(UserDTO.class))).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.createUser(testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserDTO, response.getBody());
        verify(addUserUseCase, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    void deleteUserTest() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(getUserUseCase.getUserByUsername("testuser")).thenReturn(testUserDTO);
        doNothing().when(deleteUserUseCase).deleteUser(1);

        // Act
        ResponseEntity<Void> response = userController.deleteUser(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteUserUseCase, times(1)).deleteUser(1);
    }

    @Test
    void getUserTest() {
        // Arrange
        Integer userId = 1;
        when(getUserUseCase.getUser(anyInt())).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.getUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserDTO, response.getBody());
        verify(getUserUseCase, times(1)).getUser(userId);
    }

    @Test
    void getAllUsersTest() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        List<UserDTO> userList = Arrays.asList(testUserDTO);
        when(getAllUsersUseCase.getAllUsers()).thenReturn(userList);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userList, response.getBody());
        verify(getAllUsersUseCase, times(1)).getAllUsers();
    }

    @Test
    void updateUserTest() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(getUserUseCase.getUserByUsername("testuser")).thenReturn(testUserDTO);
        when(updateUserUseCase.updateUser(1, testUserDTO)).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.updateUser(1, testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserDTO, response.getBody());
        verify(updateUserUseCase, times(1)).updateUser(1, testUserDTO);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
} 