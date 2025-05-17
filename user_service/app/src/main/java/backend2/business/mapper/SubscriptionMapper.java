package backend2.business.mapper;

import backend2.domain.SubscriptionDTO;
import backend2.persistence.entity.SubscriptionEntity;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {
    
    public SubscriptionDTO toDTO(SubscriptionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return SubscriptionDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .type(entity.getType())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .price(entity.getPrice())
                .active(entity.getActive())
                .build();
    }

    public SubscriptionEntity toEntity(SubscriptionDTO dto) {
        if (dto == null) {
            return null;
        }

        return SubscriptionEntity.builder()
                .id(dto.getId())
                .type(dto.getType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .price(dto.getPrice())
                .active(dto.getActive())
                .build();
    }
} 