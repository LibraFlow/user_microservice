package backend2.business;

import backend2.business.usecase.subscription.AddSubscriptionUseCase;
import backend2.domain.SubscriptionDTO;
import backend2.domain.SubscriptionType;
import backend2.persistence.SubscriptionRepository;
import backend2.persistence.UserRepository;
import backend2.persistence.entity.SubscriptionEntity;
import backend2.persistence.entity.UserEntity;
import backend2.business.mapper.SubscriptionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

class AddSubscriptionUseCaseTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SubscriptionMapper subscriptionMapper;
    @InjectMocks
    private AddSubscriptionUseCase addSubscriptionUseCase;

    private UserEntity testUser;
    private SubscriptionEntity testSubscriptionEntity;
    private SubscriptionDTO testSubscriptionDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = UserEntity.builder().id(1).build();
        testSubscriptionEntity = SubscriptionEntity.builder().id(10).user(testUser).type(SubscriptionType.MONTHLY).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(30)).price(10.0).active(true).build();
        testSubscriptionDTO = SubscriptionDTO.builder().id(10).userId(1).type(SubscriptionType.MONTHLY).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(30)).price(10.0).active(true).build();
    }

    @Test
    void addSubscription_ShouldLogAuditTrail() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findFirstByUserIdAndActiveTrueOrderByEndDateDesc(1)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(SubscriptionEntity.class))).thenReturn(testSubscriptionEntity);
        when(subscriptionMapper.toDTO(any(SubscriptionEntity.class))).thenReturn(testSubscriptionDTO);

        Logger logger = (Logger) LoggerFactory.getLogger(AddSubscriptionUseCase.class);
        @SuppressWarnings("unchecked")
        Appender<ILoggingEvent> mockAppender = mock(Appender.class);
        logger.addAppender(mockAppender);

        // Act
        addSubscriptionUseCase.addSubscription(1, SubscriptionType.MONTHLY);

        // Assert: verify that the audit log message was produced
        verify(mockAppender, times(1)).doAppend(argThat(event ->
            event.getFormattedMessage().contains("AUDIT: Subscription added") &&
            event.getFormattedMessage().contains("userId=1")
        ));
        logger.detachAppender(mockAppender);
    }
} 