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

@ExtendWith(MockitoExtension.class)
class AddUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AddUserUseCase addUserUseCase;

    private UserDTO testUserDTO;
    private UserEntity testUserEntity;
    private UserEntity savedUserEntity;
    private Set<Role> testRoles;

    @BeforeEach
    void setUp() {
        // Initialize test roles
        testRoles = new HashSet<>();
        testRoles.add(Role.CUSTOMER);
        
        // Initialize test data
        testUserDTO = UserDTO.builder()
                .id(1)
                .username("testuser")
                .pwd("password123".toCharArray())
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(testRoles)
                .build();

        testUserEntity = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123".toCharArray())
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(testRoles)
                .createdAt(LocalDate.now())
                .build();

        savedUserEntity = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123".toCharArray())
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
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(testUserEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);
        when(userMapper.toDTO(any(UserEntity.class))).thenReturn(testUserDTO);

        // Act
        UserDTO result = addUserUseCase.createUser(testUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDTO.getId(), result.getId());
        assertEquals(testUserDTO.getUsername(), result.getUsername());
        assertArrayEquals(testUserDTO.getPwd(), result.getPwd());
        assertEquals(testUserDTO.getEmail(), result.getEmail());
        assertEquals(testUserDTO.getAddress(), result.getAddress());
        assertEquals(testUserDTO.getPhone(), result.getPhone());
        assertEquals(testUserDTO.getRoles(), result.getRoles());

        // Verify interactions
        verify(userMapper, times(1)).toEntity(testUserDTO);
        verify(userRepository, times(1)).save(testUserEntity);
        verify(userMapper, times(1)).toDTO(savedUserEntity);
    }

    @Test
    void createUser_WithNullInput_ShouldReturnNull() {
        // Arrange
        when(userMapper.toEntity(null)).thenReturn(null);
        when(userRepository.save(null)).thenReturn(null);
        when(userMapper.toDTO(null)).thenReturn(null);

        // Act
        UserDTO result = addUserUseCase.createUser(null);

        // Assert
        assertNull(result);

        // Verify interactions
        verify(userMapper, times(1)).toEntity(null);
        verify(userRepository, times(1)).save(null);
        verify(userMapper, times(1)).toDTO(null);
    }
} 