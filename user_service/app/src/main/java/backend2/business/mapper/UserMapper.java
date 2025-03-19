package backend2.business.mapper;

import backend2.domain.UserDTO;
import backend2.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDTO toDTO(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .pwd(entity.getPwd())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .roles(entity.getRoles())
                .build();
    }

    public UserEntity toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        return UserEntity.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .pwd(dto.getPwd())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .roles(dto.getRoles())
                .createdAt(dto.getId() == null ? LocalDate.now() : null) // Set createdAt only for new entities
                .build();
    }
}