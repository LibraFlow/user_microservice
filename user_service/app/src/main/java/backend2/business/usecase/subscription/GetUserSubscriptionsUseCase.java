package backend2.business.usecase.subscription;

import backend2.domain.SubscriptionDTO;
import backend2.persistence.SubscriptionRepository;
import backend2.business.mapper.SubscriptionMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserSubscriptionsUseCase {
    
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Transactional
    public List<SubscriptionDTO> getUserSubscriptions(Integer userId) {
        return subscriptionRepository.findByUserId(userId)
                .stream()
                .map(subscriptionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<SubscriptionDTO> getActiveUserSubscriptions(Integer userId) {
        return subscriptionRepository.findByUserIdAndActiveTrue(userId)
                .stream()
                .map(subscriptionMapper::toDTO)
                .collect(Collectors.toList());
    }
} 