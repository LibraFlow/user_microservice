package backend2.business.mapper;

import backend2.domain.UserDTO;
import backend2.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    private UserEntity testUserEntity;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data for UserEntity
        testUserEntity = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("123-456-7890")
                .roles(Arrays.asList("USER", "ADMIN"))
                .createdAt(LocalDate.now())
                .build();

        // Initialize test data for UserDTO
        testUserDTO = UserDTO.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("123-456-7890")
                .roles(Arrays.asList("USER", "ADMIN"))
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
    void toDTO_WithEmptyRoles_ShouldReturnEmptyList() {
        // Arrange
        UserEntity entityWithEmptyRoles = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("123-456-7890")
                .roles(Collections.emptyList())
                .build();

        // Act
        UserDTO result = userMapper.toDTO(entityWithEmptyRoles);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoles().isEmpty());
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
                .phone("987-654-3210")
                .roles(Collections.singletonList("USER"))
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