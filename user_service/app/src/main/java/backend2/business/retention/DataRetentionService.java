package backend2.business.retention;

import backend2.config.RetentionConfig;
import backend2.persistence.UserRepository;
import backend2.persistence.SubscriptionRepository;
import backend2.persistence.entity.UserEntity;
import backend2.persistence.entity.SubscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataRetentionService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional
    public void cleanupExpiredData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate userRetentionDate = now.minusDays(RetentionConfig.DELETED_USER_RETENTION_DAYS).toLocalDate();
        LocalDateTime subscriptionRetentionDate = now.minusDays(RetentionConfig.SUBSCRIPTION_HISTORY_RETENTION_DAYS);

        // Clean up expired deleted users
        List<UserEntity> expiredDeletedUsers = userRepository.findByDeletedTrueAndDeletedAtBefore(userRetentionDate);
        for (UserEntity user : expiredDeletedUsers) {
            userRepository.delete(user);
        }

        // Clean up expired subscriptions
        List<SubscriptionEntity> expiredSubscriptions = subscriptionRepository.findByEndDateBefore(subscriptionRetentionDate);
        for (SubscriptionEntity subscription : expiredSubscriptions) {
            subscriptionRepository.delete(subscription);
        }
    }
} 