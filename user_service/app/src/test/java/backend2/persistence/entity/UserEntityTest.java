package backend2.persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import backend2.domain.Role;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserEntityTest {

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
    }

    @Test
    void testUserEntityBuilder() {
        LocalDate testDate = LocalDate.now();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        
        UserEntity builtEntity = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(roles)
                .createdAt(testDate)
                .build();

        assertNotNull(builtEntity);
        assertEquals(1, builtEntity.getId());
        assertEquals("testuser", builtEntity.getUsername());
        assertEquals("password123", builtEntity.getPwd());
        assertEquals("test@example.com", builtEntity.getEmail());
        assertEquals("123 Test St", builtEntity.getAddress());
        assertEquals("+1234567890", builtEntity.getPhone());
        assertEquals(roles, builtEntity.getRoles());
        assertEquals(testDate, builtEntity.getCreatedAt());
    }

    @Test
    void testUserEntitySettersAndGetters() {
        LocalDate testDate = LocalDate.now();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);

        userEntity.setId(1);
        userEntity.setUsername("testuser");
        userEntity.setPwd("password123");
        userEntity.setEmail("test@example.com");
        userEntity.setAddress("123 Test St");
        userEntity.setPhone("+1234567890");
        userEntity.setRoles(roles);
        userEntity.setCreatedAt(testDate);

        assertEquals(1, userEntity.getId());
        assertEquals("testuser", userEntity.getUsername());
        assertEquals("password123", userEntity.getPwd());
        assertEquals("test@example.com", userEntity.getEmail());
        assertEquals("123 Test St", userEntity.getAddress());
        assertEquals("+1234567890", userEntity.getPhone());
        assertEquals(roles, userEntity.getRoles());
        assertEquals(testDate, userEntity.getCreatedAt());
    }

    @Test
    void testUserEntityEqualsAndHashCode() {
        LocalDate testDate = LocalDate.now();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        
        UserEntity entity1 = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(roles)
                .createdAt(testDate)
                .build();

        UserEntity entity2 = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(roles)
                .createdAt(testDate)
                .build();

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testUserEntityToString() {
        LocalDate testDate = LocalDate.now();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        
        UserEntity entity = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(roles)
                .createdAt(testDate)
                .build();

        String toString = entity.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username=testuser"));
        assertTrue(toString.contains("email=test@example.com"));
        assertTrue(toString.contains("address=123 Test St"));
        assertTrue(toString.contains("phone=+1234567890"));
        assertTrue(toString.contains("createdAt=" + testDate));
        // Note: toString might not include pwd for security reasons
    }

    @Test
    void testUserEntityNoArgsConstructor() {
        UserEntity entity = new UserEntity();
        assertNotNull(entity);
    }

    @Test
    void testUserEntityAllArgsConstructor() {
        LocalDate testDate = LocalDate.now();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        
        UserEntity entity = new UserEntity(1, "testuser", "password123", 
                "test@example.com", "123 Test St", "+1234567890", roles, testDate);

        assertEquals(1, entity.getId());
        assertEquals("testuser", entity.getUsername());
        assertEquals("password123", entity.getPwd());
        assertEquals("test@example.com", entity.getEmail());
        assertEquals("123 Test St", entity.getAddress());
        assertEquals("+1234567890", entity.getPhone());
        assertEquals(roles, entity.getRoles());
        assertEquals(testDate, entity.getCreatedAt());
    }
    
    @Test
    void testUserEntityWithMultipleRoles() {
        LocalDate testDate = LocalDate.now();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        roles.add(Role.LIBRARIAN);
        
        UserEntity entity = UserEntity.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("+1234567890")
                .roles(roles)
                .createdAt(testDate)
                .build();
        
        assertEquals(2, entity.getRoles().size());
        assertTrue(entity.getRoles().contains(Role.CUSTOMER));
        assertTrue(entity.getRoles().contains(Role.LIBRARIAN));
    }
    
    @Test
    void testUserEntityWithAdminRole() {
        LocalDate testDate = LocalDate.now();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMINISTRATOR);
        
        UserEntity entity = UserEntity.builder()
                .id(1)
                .username("admin")
                .pwd("adminpass")
                .email("admin@example.com")
                .address("Admin St")
                .phone("+1234567890")
                .roles(roles)
                .createdAt(testDate)
                .build();
        
        assertEquals(1, entity.getRoles().size());
        assertTrue(entity.getRoles().contains(Role.ADMINISTRATOR));
    }
} 