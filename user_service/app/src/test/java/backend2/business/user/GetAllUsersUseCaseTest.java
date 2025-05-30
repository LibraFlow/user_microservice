package backend2.business.usecase.user;

import backend2.domain.UserDTO;
import backend2.domain.Role;
import backend2.persistence.UserRepository;
import backend2.persistence.entity.UserEntity;
import backend2.business.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllUsersUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private GetAllUsersUseCase getAllUsersUseCase;

    private UserEntity testUserEntity1;
    private UserEntity testUserEntity2;
    private UserDTO testUserDTO1;
    private UserDTO testUserDTO2;
    private Set<Role> testRoles;

    @BeforeEach
    void setUp() {
        // Initialize test roles
        testRoles = new HashSet<>();
        testRoles.add(Role.CUSTOMER);
        
        // Initialize test data for UserEntity1
        testUserEntity1 = UserEntity.builder()
                .id(1)
                .username("user1")
                .pwd("password1")
                .email("user1@example.com")
                .address("123 User1 St")
                .phone("+1234567890")
                .roles(testRoles)
                .createdAt(LocalDate.now())
                .build();

        // Initialize test data for UserEntity2
        testUserEntity2 = UserEntity.builder()
                .id(2)
                .username("user2")
                .pwd("password2")
                .email("user2@example.com")
                .address("456 User2 St")
                .phone("+0987654321")
                .roles(testRoles)
                .createdAt(LocalDate.now())
                .build();

        // Initialize test data for UserDTO1
        testUserDTO1 = UserDTO.builder()
                .id(1)
                .username("user1")
                .pwd("password1")
                .email("user1@example.com")
                .address("123 User1 St")
                .phone("+1234567890")
                .roles(testRoles)
                .build();

        // Initialize test data for UserDTO2
        testUserDTO2 = UserDTO.builder()
                .id(2)
                .username("user2")
                .pwd("password2")
                .email("user2@example.com")
                .address("456 User2 St")
                .phone("+0987654321")
                .roles(testRoles)
                .build();
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        List<UserEntity> userEntities = Arrays.asList(testUserEntity1, testUserEntity2);
        when(userRepository.findAll()).thenReturn(userEntities);
        when(userMapper.toDTO(testUserEntity1)).thenReturn(testUserDTO1);
        when(userMapper.toDTO(testUserEntity2)).thenReturn(testUserDTO2);

        // Act
        List<UserDTO> result = getAllUsersUseCase.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUserDTO1, result.get(0));
        assertEquals(testUserDTO2, result.get(1));

        // Verify interactions
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDTO(testUserEntity1);
        verify(userMapper, times(1)).toDTO(testUserEntity2);
    }

    @Test
    void getAllUsers_EmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<UserDTO> result = getAllUsersUseCase.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions
        verify(userRepository, times(1)).findAll();
        verify(userMapper, never()).toDTO(any());
    }
} 