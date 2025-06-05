package backend2.presentation;

import backend2.domain.SubscriptionDTO;
import backend2.domain.SubscriptionType;
import backend2.business.usecase.subscription.AddSubscriptionUseCase;
import backend2.business.usecase.subscription.GetUserSubscriptionsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @Mock
    private AddSubscriptionUseCase addSubscriptionUseCase;
    @Mock
    private GetUserSubscriptionsUseCase getUserSubscriptionsUseCase;
    @InjectMocks
    private SubscriptionController subscriptionController;

    private Authentication authentication;
    private SecurityContext securityContext;
    private Jwt jwt;
    private SubscriptionDTO testSubscriptionDTO;

    @BeforeEach
    void setUp() {
        testSubscriptionDTO = SubscriptionDTO.builder()
                .id(1)
                .userId(1)
                .type(SubscriptionType.MONTHLY)
                .active(true)
                .build();
        jwt = mock(Jwt.class);
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);
        when(jwt.getClaim("userId")).thenReturn(1);
    }

    @Test
    void addSubscriptionTest() {
        when(addSubscriptionUseCase.addSubscription(1, SubscriptionType.MONTHLY)).thenReturn(testSubscriptionDTO);
        ResponseEntity<SubscriptionDTO> response = subscriptionController.addSubscription(1, SubscriptionType.MONTHLY);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testSubscriptionDTO, response.getBody());
    }

    @Test
    void getUserSubscriptionsTest() {
        when(getUserSubscriptionsUseCase.getUserSubscriptions(1)).thenReturn(Collections.singletonList(testSubscriptionDTO));
        ResponseEntity<List<SubscriptionDTO>> response = subscriptionController.getUserSubscriptions(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testSubscriptionDTO, response.getBody().get(0));
    }

    @Test
    void getActiveUserSubscriptionsTest() {
        when(getUserSubscriptionsUseCase.getActiveUserSubscriptions(1)).thenReturn(Collections.singletonList(testSubscriptionDTO));
        ResponseEntity<List<SubscriptionDTO>> response = subscriptionController.getActiveUserSubscriptions(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testSubscriptionDTO, response.getBody().get(0));
    }
} 