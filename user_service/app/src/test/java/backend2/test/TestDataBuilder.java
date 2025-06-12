package backend2.test;

import backend2.domain.Role;
import backend2.domain.SubscriptionType;
import backend2.domain.UserDTO;
import backend2.persistence.entity.UserEntity;
import backend2.persistence.entity.SubscriptionEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TestDataBuilder {
    
    public static UserDTO createTestUserDTO() {
        Set<String> roles = new HashSet<>();
        roles.add(Role.CUSTOMER.name());
        
        return UserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .pwd("Password123!")
                .address("123 Test St")
                .phone("1234567890")
                .roles(roles)
                .deleted(false)
                .build();
    }

    public static UserEntity createTestUserEntity() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        
        return UserEntity.builder()
                .username("testuser")
                .email("test@example.com")
                .pwd("encodedPassword")
                .address("123 Test St")
                .phone("1234567890")
                .roles(roles)
                .createdAt(LocalDate.now())
                .deleted(false)
                .build();
    }

    public static SubscriptionEntity createTestSubscriptionEntity(UserEntity user) {
        return SubscriptionEntity.builder()
                .user(user)
                .type(SubscriptionType.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .price(9.99)
                .active(true)
                .build();
    }
} 