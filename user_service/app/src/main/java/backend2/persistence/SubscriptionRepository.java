package backend2.persistence;

import backend2.persistence.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Integer> {
    List<SubscriptionEntity> findByUserId(Integer userId);
    List<SubscriptionEntity> findByUserIdAndActiveTrue(Integer userId);
    Optional<SubscriptionEntity> findFirstByUserIdAndActiveTrueOrderByEndDateDesc(Integer userId);
    List<SubscriptionEntity> findByEndDateBefore(LocalDateTime date);
} 