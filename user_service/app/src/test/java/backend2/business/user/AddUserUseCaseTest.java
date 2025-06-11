package backend2.business.user;

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
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import backend2.business.usecase.auth.RegisterUserUseCase;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private UserDTO testUserDTO;
    private UserEntity testUserEntity;
    private UserEntity savedUserEntity;
    private Set<String> testRolesString;

    @BeforeEach
    void setUp() {
        // Initialize test roles
        Set<Role> testRoles = new HashSet<>();
        testRoles.add(Role.CUSTOMER);
        testRolesString = new HashSet<>();
        testRolesString.add("CUSTOMER");
        
        // Initialize test data
        testUserDTO = UserDTO.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(testRolesString)
                .build();

        testUserEntity = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(testRoles)
                .createdAt(LocalDate.now())
                .build();

        savedUserEntity = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(testRoles)
                .createdAt(LocalDate.now())
                .build();
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userMapper.toEntity(testUserDTO)).thenReturn(testUserEntity);
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDTO(testUserEntity)).thenReturn(testUserDTO);

        // Act
        UserDTO result = registerUserUseCase.createUser(testUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDTO.getId(), result.getId());
        assertEquals(testUserDTO.getUsername(), result.getUsername());
        assertEquals(testUserDTO.getPwd(), result.getPwd());
        assertEquals(testUserDTO.getEmail(), result.getEmail());
        assertEquals(testUserDTO.getAddress(), result.getAddress());
        assertEquals(testUserDTO.getPhone(), result.getPhone());
        assertEquals(testUserDTO.getRoles(), result.getRoles());

        // Verify
        verify(userMapper, times(1)).toEntity(testUserDTO);
        verify(userRepository, times(1)).save(testUserEntity);
        verify(userMapper, times(1)).toDTO(testUserEntity);
    }

    @Test
    void createUser_WithNullInput_ShouldReturnNull() {
        // Arrange
        when(userMapper.toEntity(null)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            registerUserUseCase.createUser(null);
        });

        // Verify
        verify(userMapper, times(1)).toEntity(null);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDTO(any());
    }
} 