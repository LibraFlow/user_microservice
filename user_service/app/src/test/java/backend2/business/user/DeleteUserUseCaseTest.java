package backend2.business.user;

import backend2.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

    private Integer testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1;
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById(testUserId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(testUserId);

        // Act
        Void result = deleteUserUseCase.deleteUser(testUserId);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).existsById(testUserId);
        verify(userRepository, times(1)).deleteById(testUserId);
    }

    @Test
    void deleteUser_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.existsById(testUserId)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            deleteUserUseCase.deleteUser(testUserId);
        });

        assertEquals("User not found with id: " + testUserId, exception.getMessage());
        verify(userRepository, times(1)).existsById(testUserId);
        verify(userRepository, never()).deleteById(any());
    }
} 