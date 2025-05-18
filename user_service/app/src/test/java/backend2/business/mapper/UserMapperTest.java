package backend2.business.mapper;

import backend2.domain.Role;
import backend2.domain.UserDTO;
import backend2.persistence.entity.UserEntity;
import backend2.security.EncryptionService;
import backend2.security.PasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserMapperTest {

    @Mock
    private EncryptionService encryptionService;
    @Mock
    private PasswordEncoderService passwordEncoderService;

    private UserMapper userMapper;

    private UserEntity testUserEntity;
    private UserDTO testUserDTO;
    private Set<Role> testRoles;

    @BeforeEach
    void setUp() {
        // Mock encryptionService to return the input value for encrypt/decrypt
        when(encryptionService.encrypt(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(encryptionService.decrypt(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        // Mock passwordEncoderService to return the input value for encode, and always match
        when(passwordEncoderService.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoderService.matches(anyString(), anyString())).thenReturn(true);
        userMapper = new UserMapper(encryptionService, passwordEncoderService);

        // Initialize test roles
        testRoles = new HashSet<>();
        testRoles.add(Role.CUSTOMER);
        testRoles.add(Role.LIBRARIAN);

        // Initialize test data for UserEntity
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

        // Initialize test data for UserDTO
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
    void toDTO_WithValidEntity_ShouldReturnCorrectDTO() {
        // Act
        UserDTO result = userMapper.toDTO(testUserEntity);

        // Assert
        assertNotNull(result);
        assertEquals(testUserEntity.getId(), result.getId());
        assertEquals(testUserEntity.getUsername(), result.getUsername());
        assertEquals(testUserEntity.getPwd(), result.getPwd());
        assertEquals(testUserEntity.getEmail(), result.getEmail());
        assertEquals(testUserEntity.getAddress(), result.getAddress());
        assertEquals(testUserEntity.getPhone(), result.getPhone());
        assertEquals(testUserEntity.getRoles(), result.getRoles());
    }

    @Test
    void toDTO_WithNullEntity_ShouldReturnNull() {
        // Act
        UserDTO result = userMapper.toDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_WithValidDTO_ShouldReturnCorrectEntity() {
        // Act
        UserEntity result = userMapper.toEntity(testUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDTO.getId(), result.getId());
        assertEquals(testUserDTO.getUsername(), result.getUsername());
        assertEquals(testUserDTO.getPwd(), result.getPwd());
        assertEquals(testUserDTO.getEmail(), result.getEmail());
        assertEquals(testUserDTO.getAddress(), result.getAddress());
        assertEquals(testUserDTO.getPhone(), result.getPhone());
        assertEquals(testUserDTO.getRoles(), result.getRoles());
        assertNull(result.getCreatedAt()); // Existing user should not have createdAt set
    }

    @Test
    void toEntity_WithNullDTO_ShouldReturnNull() {
        // Act
        UserEntity result = userMapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_WithNewUser_ShouldSetCreatedAt() {
        // Arrange
        UserDTO newUserDTO = UserDTO.builder()
                .username("newuser")
                .pwd("newpassword")
                .email("new@example.com")
                .address("456 New St")
                .phone("+9876543210")
                .roles(testRoles)
                .build();

        // Act
        UserEntity result = userMapper.toEntity(newUserDTO);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCreatedAt());
        assertEquals(LocalDate.now(), result.getCreatedAt());
    }

    @Test
    void toEntity_WithExistingUser_ShouldNotSetCreatedAt() {
        // Act
        UserEntity result = userMapper.toEntity(testUserDTO);

        // Assert
        assertNotNull(result);
        assertNull(result.getCreatedAt());
    }
} 