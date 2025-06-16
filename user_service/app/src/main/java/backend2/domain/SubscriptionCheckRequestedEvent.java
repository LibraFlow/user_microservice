package backend2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionCheckRequestedEvent {
    private Integer userId;
    private String correlationId;
} 