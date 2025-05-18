package backend2.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

class UserDTOTest {

    @Test
    void testUserDTOBuilder() {
        // Arrange & Act
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        
        UserDTO userDTO = UserDTO.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("123-456-7890")
                .roles(roles)
                .build();

        // Assert
        assertNotNull(userDTO);
        assertEquals(1, userDTO.getId());
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("password123", userDTO.getPwd());
        assertEquals("test@example.com", userDTO.getEmail());
        assertEquals("123 Test St", userDTO.getAddress());
        assertEquals("123-456-7890", userDTO.getPhone());
        assertEquals(roles, userDTO.getRoles());
    }

    @Test
    void testUserDTOAllArgsConstructor() {
        // Arrange & Act
        Set<Role> roles = new HashSet<>();
        roles.add(Role.LIBRARIAN);
        
        UserDTO userDTO = new UserDTO(1, "testuser", "password123", 
                "test@example.com", "123 Test St", "123-456-7890", roles);

        // Assert
        assertNotNull(userDTO);
        assertEquals(1, userDTO.getId());
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("password123", userDTO.getPwd());
        assertEquals("test@example.com", userDTO.getEmail());
        assertEquals("123 Test St", userDTO.getAddress());
        assertEquals("123-456-7890", userDTO.getPhone());
        assertEquals(roles, userDTO.getRoles());
    }

    @Test
    void testUserDTOEqualsAndHashCode() {
        // Arrange
        Set<Role> roles1 = new HashSet<>();
        roles1.add(Role.CUSTOMER);
        
        Set<Role> roles2 = new HashSet<>();
        roles2.add(Role.CUSTOMER);
        
        Set<Role> roles3 = new HashSet<>();
        roles3.add(Role.ADMINISTRATOR);

        UserDTO userDTO1 = UserDTO.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("123-456-7890")
                .roles(roles1)
                .build();

        UserDTO userDTO2 = UserDTO.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("123-456-7890")
                .roles(roles2)
                .build();

        UserDTO userDTO3 = UserDTO.builder()
                .id(2)
                .username("differentuser")
                .pwd("differentpass")
                .email("different@example.com")
                .address("456 Different St")
                .phone("987-654-3210")
                .roles(roles3)
                .build();

        // Assert
        assertEquals(userDTO1, userDTO2);
        assertNotEquals(userDTO1, userDTO3);
        assertEquals(userDTO1.hashCode(), userDTO2.hashCode());
        assertNotEquals(userDTO1.hashCode(), userDTO3.hashCode());
    }

    @Test
    void testUserDTOToString() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        
        UserDTO userDTO = UserDTO.builder()
                .id(1)
                .username("testuser")
                .pwd("password123")
                .email("test@example.com")
                .address("123 Test St")
                .phone("123-456-7890")
                .roles(roles)
                .build();

        // Act
        String toString = userDTO.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username=testuser"));
        assertTrue(toString.contains("email=test@example.com"));
        assertTrue(toString.contains("address=123 Test St"));
        assertTrue(toString.contains("phone=123-456-7890"));
        assertTrue(toString.contains("roles="));
    }
    
    @Test
    void testUserDTOSettersAndGetters() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMINISTRATOR);
        
        // Act
        userDTO.setId(1);
        userDTO.setUsername("testuser");
        userDTO.setPwd("password123");
        userDTO.setEmail("test@example.com");
        userDTO.setAddress("123 Test St");
        userDTO.setPhone("123-456-7890");
        userDTO.setRoles(roles);
        
        // Assert
        assertEquals(1, userDTO.getId());
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("password123", userDTO.getPwd());
        assertEquals("test@example.com", userDTO.getEmail());
        assertEquals("123 Test St", userDTO.getAddress());
        assertEquals("123-456-7890", userDTO.getPhone());
        assertEquals(roles, userDTO.getRoles());
    }
} 