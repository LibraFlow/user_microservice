package backend2.business;

import backend2.domain.SubscriptionCheckRequestedEvent;
import backend2.domain.SubscriptionCheckResultEvent;
import backend2.business.usecase.subscription.GetUserSubscriptionsUseCase;
import backend2.domain.SubscriptionDTO;
import backend2.config.KafkaConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionCheckEventListener {
    private final GetUserSubscriptionsUseCase getUserSubscriptionsUseCase;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaConfig.SUBSCRIPTION_CHECK_REQUESTED_TOPIC, groupId = "libraflow-group")
    public void handleSubscriptionCheckRequested(String json) {
        try {
            SubscriptionCheckRequestedEvent event = objectMapper.readValue(json, SubscriptionCheckRequestedEvent.class);
            List<SubscriptionDTO> activeSubs = getUserSubscriptionsUseCase.getActiveUserSubscriptions(event.getUserId());
            boolean hasActive = activeSubs != null && activeSubs.stream().anyMatch(sub -> sub.getEndDate() != null && sub.getEndDate().isAfter(LocalDateTime.now()));
            LocalDate subscriptionEndDate = null;
            if (hasActive) {
                subscriptionEndDate = activeSubs.stream()
                    .filter(sub -> sub.getEndDate() != null && sub.getEndDate().isAfter(LocalDateTime.now()))
                    .map(sub -> sub.getEndDate().toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(null);
            }
            SubscriptionCheckResultEvent result = new SubscriptionCheckResultEvent(event.getUserId(), event.getCorrelationId(), hasActive, subscriptionEndDate);
            String resultJson = objectMapper.writeValueAsString(result);
            kafkaTemplate.send(KafkaConfig.SUBSCRIPTION_CHECK_RESULT_TOPIC, resultJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 