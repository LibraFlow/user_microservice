package backend2.business.mapper;

import backend2.domain.UserDTO;
import backend2.persistence.entity.UserEntity;
import backend2.security.EncryptionService;
import backend2.security.PasswordEncoderService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.stream.Collectors;

import backend2.domain.Role;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final EncryptionService encryptionService;
    private final PasswordEncoderService passwordEncoderService;

    public UserDTO toDTO(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .pwd(entity.getPwd())
                .email(encryptionService.decrypt(entity.getEmail()))
                .address(encryptionService.decrypt(entity.getAddress()))
                .phone(encryptionService.decrypt(entity.getPhone()))
                .roles(entity.getRoles().stream().map(Role::name).collect(Collectors.toSet()))
                .deleted(entity.isDeleted())
                .build();
    }

    public UserEntity toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        return UserEntity.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .pwd(passwordEncoderService.encode(dto.getPwd()))
                .email(encryptionService.encrypt(dto.getEmail()))
                .address(encryptionService.encrypt(dto.getAddress()))
                .phone(encryptionService.encrypt(dto.getPhone()))
                .roles(dto.getRoles().stream().map(Role::valueOf).collect(Collectors.toSet()))
                .createdAt(dto.getId() == null ? LocalDate.now() : null)
                .deleted(dto.isDeleted())
                .build();
    }
}