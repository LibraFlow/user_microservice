package backend2.business.user;

import backend2.domain.SubscriptionDTO;
import backend2.domain.SubscriptionType;
import backend2.persistence.SubscriptionRepository;
import backend2.persistence.UserRepository;
import backend2.persistence.entity.SubscriptionEntity;
import backend2.persistence.entity.UserEntity;
import backend2.business.mapper.SubscriptionMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AddSubscriptionUseCase {
    
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Transactional
    public SubscriptionDTO addSubscription(Integer userId, SubscriptionType type) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Get current active subscription
        SubscriptionEntity currentActiveSubscription = subscriptionRepository
                .findFirstByUserIdAndActiveTrueOrderByEndDateDesc(userId)
                .orElse(null);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (currentActiveSubscription != null) {
            // Check if current subscription is within 2 weeks of expiration
            long daysUntilExpiration = ChronoUnit.DAYS.between(now, currentActiveSubscription.getEndDate());
            
            if (daysUntilExpiration > 14) {
                throw new IllegalStateException("Cannot create new subscription. Current subscription expires in " + 
                    daysUntilExpiration + " days. New subscriptions can only be created within 2 weeks of expiration.");
            }

            // New subscription starts when current one expires
            startDate = currentActiveSubscription.getEndDate();
            endDate = startDate.plusDays(type.getDurationInDays());

            // Deactivate current subscription
            currentActiveSubscription.setActive(false);
            subscriptionRepository.save(currentActiveSubscription);
        } else {
            // No active subscription, start immediately
            startDate = now;
            endDate = now.plusDays(type.getDurationInDays());
        }

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .user(user)
                .type(type)
                .startDate(startDate)
                .endDate(endDate)
                .price(type.getPrice())
                .active(true)
                .build();

        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDTO(savedSubscription);
    }
} 