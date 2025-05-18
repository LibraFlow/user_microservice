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
class UpdateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    private UserEntity existingUserEntity;
    private UserEntity updatedUserEntity;
    private UserDTO updatedUserDTO;
    private Set<Role> testRoles;
    private Integer testUserId;
    private LocalDate testCreatedAt;

    @BeforeEach
    void setUp() {
        // Initialize test roles
        testRoles = new HashSet<>();
        testRoles.add(Role.CUSTOMER);
        
        // Initialize test user ID
        testUserId = 1;
        
        // Initialize test creation date
        testCreatedAt = LocalDate.of(2020, 1, 1);
        
        // Initialize test data for existing UserEntity
        existingUserEntity = UserEntity.builder()
                .id(testUserId)
                .username("oldusername")
                .pwd("oldpassword")
                .email("old@example.com")
                .address("123 Old St")
                .phone("+1234567890")
                .roles(testRoles)
                .createdAt(testCreatedAt)
                .build();

        // Initialize test data for updated UserEntity
        updatedUserEntity = UserEntity.builder()
                .id(testUserId)
                .username("newusername")
                .pwd("newpassword")
                .email("new@example.com")
                .address("456 New St")
                .phone("+0987654321")
                .roles(testRoles)
                .createdAt(testCreatedAt)
                .build();

        // Initialize test data for updated UserDTO
        updatedUserDTO = UserDTO.builder()
                .id(testUserId)
                .username("newusername")
                .pwd("newpassword")
                .email("new@example.com")
                .address("456 New St")
                .phone("+0987654321")
                .roles(testRoles)
                .build();
    }

    @Test
    void updateUser_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(existingUserEntity));
        when(userMapper.toEntity(updatedUserDTO)).thenReturn(updatedUserEntity);
        when(userRepository.save(updatedUserEntity)).thenReturn(updatedUserEntity);
        when(userMapper.toDTO(updatedUserEntity)).thenReturn(updatedUserDTO);

        // Act
        UserDTO result = updateUserUseCase.updateUser(testUserId, updatedUserDTO, false);

        // Assert
        assertNotNull(result);
        assertEquals(updatedUserDTO.getId(), result.getId());
        assertEquals(updatedUserDTO.getUsername(), result.getUsername());
        assertEquals(updatedUserDTO.getPwd(), result.getPwd());
        assertEquals(updatedUserDTO.getEmail(), result.getEmail());
        assertEquals(updatedUserDTO.getAddress(), result.getAddress());
        assertEquals(updatedUserDTO.getPhone(), result.getPhone());
        assertEquals(updatedUserDTO.getRoles(), result.getRoles());

        // Verify interactions
        verify(userRepository, times(1)).findById(testUserId);
        verify(userMapper, times(1)).toEntity(updatedUserDTO);
        verify(userRepository, times(1)).save(updatedUserEntity);
        verify(userMapper, times(1)).toDTO(updatedUserEntity);
    }

    @Test
    void updateUser_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            updateUserUseCase.updateUser(testUserId, updatedUserDTO, false);
        });

        assertEquals("User not found with id: " + testUserId, exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).findById(testUserId);
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDTO(any());
    }
} 