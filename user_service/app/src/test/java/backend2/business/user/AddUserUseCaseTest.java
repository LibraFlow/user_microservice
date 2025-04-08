package backend2.business.user;

import backend2.domain.UserDTO;
import backend2.persistence.UserRepository;
import backend2.persistence.entity.UserEntity;
import backend2.business.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @BeforeEach
    void setUp() {
        // Initialize test data
        testUserDTO = UserDTO.builder()
                .id(1)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .dateOfBirth(java.time.LocalDate.of(1990, 1, 1))
                .build();

        testUserEntity = new UserEntity();
        testUserEntity.setId(1);
        testUserEntity.setUsername("testuser");
        testUserEntity.setEmail("test@example.com");
        testUserEntity.setPassword("password123");
        testUserEntity.setFirstName("Test");
        testUserEntity.setLastName("User");
        testUserEntity.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));

        savedUserEntity = new UserEntity();
        savedUserEntity.setId(1);
        savedUserEntity.setUsername("testuser");
        savedUserEntity.setEmail("test@example.com");
        savedUserEntity.setPassword("password123");
        savedUserEntity.setFirstName("Test");
        savedUserEntity.setLastName("User");
        savedUserEntity.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
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
        assertEquals(testUserDTO.getEmail(), result.getEmail());
        assertEquals(testUserDTO.getFirstName(), result.getFirstName());
        assertEquals(testUserDTO.getLastName(), result.getLastName());
        assertEquals(testUserDTO.getDateOfBirth(), result.getDateOfBirth());

        // Verify interactions
        verify(userMapper, times(1)).toEntity(testUserDTO);
        verify(userRepository, times(1)).save(testUserEntity);
        verify(userMapper, times(1)).toDTO(savedUserEntity);
    }

    @Test
    void createUser_WithNullInput_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            addUserUseCase.createUser(null);
        });

        // Verify no interactions with dependencies
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }
} 