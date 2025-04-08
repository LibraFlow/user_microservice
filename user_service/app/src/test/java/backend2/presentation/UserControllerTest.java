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
                .pwd("password123".toCharArray())
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
        Integer userId = 1;
        doNothing().when(deleteUserUseCase).deleteUser(anyInt());

        // Act
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteUserUseCase, times(1)).deleteUser(userId);
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
        when(updateUserUseCase.updateUser(anyInt(), any(UserDTO.class))).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.updateUser(1, testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserDTO, response.getBody());
        verify(updateUserUseCase, times(1)).updateUser(anyInt(), any(UserDTO.class));
    }
} 