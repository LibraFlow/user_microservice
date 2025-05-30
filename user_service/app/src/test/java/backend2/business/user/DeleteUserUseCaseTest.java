package backend2.business.usecase.user;

import backend2.domain.UserDTO;
import backend2.domain.Role;
import backend2.persistence.UserRepository;
import backend2.persistence.entity.UserEntity;
import backend2.business.mapper.UserMapper;
import backend2.exception.ResourceNotFoundException;
import backend2.security.EncryptionService;
import backend2.security.PasswordEncoderService;
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
class RightToBeForgottenUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @InjectMocks
    private RightToBeForgottenUseCase rightToBeForgottenUseCase;

    private UserEntity testUserEntity;
    private UserDTO testUserDTO;
    private Integer testUserId;
    private Set<Role> testRoles;

    @BeforeEach
    void setUp() {
        testUserId = 1;
        testRoles = new HashSet<>();
        testRoles.add(Role.CUSTOMER);
        
        testUserEntity = UserEntity.builder()
                .id(testUserId)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(testRoles)
                .build();
                
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
    void exerciseRightToBeForgotten_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        when(encryptionService.encrypt(anyString())).thenAnswer(i -> i.getArgument(0));
        when(passwordEncoderService.encode(anyString())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);

        // Act
        rightToBeForgottenUseCase.exerciseRightToBeForgotten(testUserId);

        // Verify
        verify(userRepository, times(1)).findById(testUserId);
        verify(encryptionService, times(3)).encrypt(anyString());
        verify(passwordEncoderService, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void exerciseRightToBeForgotten_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            rightToBeForgottenUseCase.exerciseRightToBeForgotten(testUserId);
        });
    }
} 