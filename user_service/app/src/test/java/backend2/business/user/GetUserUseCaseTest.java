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
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    private UserEntity testUserEntity;
    private UserDTO testUserDTO;
    private Set<Role> testRoles;
    private Integer testUserId;

    @BeforeEach
    void setUp() {
        // Initialize test roles
        testRoles = new HashSet<>();
        testRoles.add(Role.CUSTOMER);
        
        // Initialize test user ID
        testUserId = 1;
        
        // Initialize test data for UserEntity
        testUserEntity = UserEntity.builder()
                .id(testUserId)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(testRoles)
                .createdAt(LocalDate.now())
                .build();

        // Initialize test data for UserDTO
        testUserDTO = UserDTO.builder()
                .id(testUserId)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(testRoles)
                .build();
    }

    @Test
    void getUser_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(userMapper.toDTO(testUserEntity)).thenReturn(testUserDTO);

        // Act
        UserDTO result = getUserUseCase.getUser(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDTO.getId(), result.getId());
        assertEquals(testUserDTO.getUsername(), result.getUsername());
        assertEquals(testUserDTO.getPwd(), result.getPwd());
        assertEquals(testUserDTO.getEmail(), result.getEmail());
        assertEquals(testUserDTO.getAddress(), result.getAddress());
        assertEquals(testUserDTO.getPhone(), result.getPhone());
        assertEquals(testUserDTO.getRoles(), result.getRoles());

        // Verify interactions
        verify(userRepository, times(1)).findById(testUserId);
        verify(userMapper, times(1)).toDTO(testUserEntity);
    }

    @Test
    void getUser_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            getUserUseCase.getUser(testUserId);
        });

        assertEquals("User not found with id: " + testUserId, exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).findById(testUserId);
        verify(userMapper, never()).toDTO(any());
    }
} 