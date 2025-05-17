package backend2.domain;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Integer id;
    private Integer userId;
    private SubscriptionType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double price;
    private Boolean active;
} 