package backend2.business.user;

import backend2.domain.UserDTO;
import backend2.persistence.entity.UserEntity;
import backend2.persistence.UserRepository;
import backend2.business.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.owasp.encoder.Encode;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO updateUser(Integer id, UserDTO userDto) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update existing entity with DTO values
        UserEntity updatedEntity = userMapper.toEntity(userDto);
        updatedEntity.setId(id); // Ensure we keep the same ID
        updatedEntity.setCreatedAt(existingUser.getCreatedAt()); // Preserve creation date

        UserEntity savedUser = userRepository.save(updatedEntity);
        return userMapper.toDTO(savedUser);
    }
}