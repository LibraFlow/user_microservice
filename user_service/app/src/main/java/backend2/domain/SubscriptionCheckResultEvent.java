package backend2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionCheckResultEvent {
    private Integer userId;
    private String correlationId;
    private boolean hasActiveSubscription;
    private LocalDate subscriptionEndDate; // nullable, only set if hasActiveSubscription is true
} 