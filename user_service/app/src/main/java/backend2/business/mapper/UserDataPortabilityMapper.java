package backend2.business.mapper;

import backend2.domain.UserDataPortabilityDTO;
import backend2.persistence.entity.UserEntity;
import backend2.security.EncryptionService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserDataPortabilityMapper {
    private final EncryptionService encryptionService;

    public UserDataPortabilityDTO toDTO(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserDataPortabilityDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(encryptionService.decrypt(entity.getEmail()))
                .address(encryptionService.decrypt(entity.getAddress()))
                .phone(encryptionService.decrypt(entity.getPhone()))
                .roles(entity.getRoles())
                .createdAt(entity.getCreatedAt())
                .build();
    }
} 