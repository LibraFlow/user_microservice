package backend2.business.usecase.user;

import backend2.domain.UserDTO;
import backend2.domain.Role;
import backend2.persistence.UserRepository;
import backend2.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import backend2.business.usecase.auth.RegisterUserUseCase;
import backend2.business.usecase.user.GetUserUseCase;
import backend2.business.usecase.user.UpdateUserUseCase;
import backend2.business.usecase.user.RightToBeForgottenUseCase;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private GetUserUseCase getUserUseCase;

    @Autowired
    private UpdateUserUseCase updateUserUseCase;

    @Autowired
    private RightToBeForgottenUseCase rightToBeForgottenUseCase;

    @Autowired
    private UserRepository userRepository;

    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUserDTO = UserDTO.builder()
                .username("integrationuser")
                .pwd("Password123!")
                .email("integration@example.com")
                .address("123 Integration St")
                .phone("+1234567890")
                .roles(Collections.singleton("CUSTOMER"))
                .build();
    }

    @Test
    void registerAndFetchUser() {
        // Register user
        UserDTO created = registerUserUseCase.createUser(testUserDTO);
        assertNotNull(created.getId());
        assertEquals(testUserDTO.getUsername(), created.getUsername());
        assertEquals(testUserDTO.getEmail(), created.getEmail());

        // Fetch by ID
        UserDTO byId = getUserUseCase.getUser(created.getId());
        assertEquals(created.getUsername(), byId.getUsername());
        assertEquals(created.getEmail(), byId.getEmail());

        // Fetch by username
        UserDTO byUsername = getUserUseCase.getUserByUsername(created.getUsername());
        assertEquals(created.getId(), byUsername.getId());
    }

    @Test
    void updateUserTest() {
        // Register user
        UserDTO created = registerUserUseCase.createUser(testUserDTO);
        Integer userId = created.getId();
        // Update details
        UserDTO updatedDTO = UserDTO.builder()
                .id(userId)
                .username("updateduser")
                .pwd("Password123!")
                .email("updated@example.com")
                .address("456 Updated St")
                .phone("+1987654321")
                .roles(Collections.singleton("CUSTOMER"))
                .build();
        UserDTO updated = updateUserUseCase.updateUser(userId, updatedDTO, true);
        assertEquals("updateduser", updated.getUsername());
        assertEquals("updated@example.com", updated.getEmail());
        // Fetch and verify
        UserDTO byId = getUserUseCase.getUser(userId);
        assertEquals("updateduser", byId.getUsername());
        assertEquals("updated@example.com", byId.getEmail());
    }

    @Test
    void softDeleteAndRightToBeForgottenTest() {
        // Register user
        UserDTO created = registerUserUseCase.createUser(testUserDTO);
        Integer userId = created.getId();
        // Soft delete (simulate by setting deleted=true)
        UserEntity entity = userRepository.findById(userId).orElseThrow();
        entity.setDeleted(true);
        userRepository.save(entity);
        // Should not be returned by findByUsernameAndDeletedFalse
        assertThrows(Exception.class, () -> getUserUseCase.getUserByUsernameIfNotDeleted(created.getUsername()));
        // Exercise right to be forgotten
        rightToBeForgottenUseCase.exerciseRightToBeForgotten(userId);
        UserEntity anonymized = userRepository.findById(userId).orElseThrow();
        assertTrue(anonymized.getUsername().startsWith("deleted_user_"));
        assertTrue(anonymized.isDeleted());
    }
} 