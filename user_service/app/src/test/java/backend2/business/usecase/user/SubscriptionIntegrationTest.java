package backend2.business.usecase.user;

import backend2.domain.UserDTO;
import backend2.domain.SubscriptionDTO;
import backend2.domain.SubscriptionType;
import backend2.persistence.UserRepository;
import backend2.persistence.SubscriptionRepository;
import backend2.business.usecase.auth.RegisterUserUseCase;
import backend2.business.usecase.subscription.AddSubscriptionUseCase;
import backend2.business.usecase.subscription.GetUserSubscriptionsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SubscriptionIntegrationTest {
    @Autowired
    private RegisterUserUseCase registerUserUseCase;
    @Autowired
    private AddSubscriptionUseCase addSubscriptionUseCase;
    @Autowired
    private GetUserSubscriptionsUseCase getUserSubscriptionsUseCase;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUserDTO = UserDTO.builder()
                .username("subintegrationuser")
                .pwd("Password123!")
                .email("subintegration@example.com")
                .address("123 SubIntegration St")
                .phone("+1234567890")
                .roles(Collections.singleton("CUSTOMER"))
                .build();
    }

    @Test
    void addSubscriptionAndVerify() {
        // Register user
        UserDTO created = registerUserUseCase.createUser(testUserDTO);
        Integer userId = created.getId();
        // Add subscription
        SubscriptionDTO subscription = addSubscriptionUseCase.addSubscription(userId, SubscriptionType.MONTHLY);
        assertNotNull(subscription.getId());
        assertEquals(userId, subscription.getUserId());
        assertEquals(SubscriptionType.MONTHLY, subscription.getType());
        // Fetch subscriptions for user
        List<SubscriptionDTO> userSubscriptions = getUserSubscriptionsUseCase.getUserSubscriptions(userId);
        assertFalse(userSubscriptions.isEmpty());
        assertEquals(subscription.getId(), userSubscriptions.get(0).getId());
    }
} 