package backend2.business.user;

import backend2.persistence.entity.UserEntity;
import backend2.domain.UserDTO;
import backend2.persistence.UserRepository;
import backend2.business.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.owasp.encoder.Encode;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AddUserUseCase {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = userMapper.toEntity(userDTO);
        userEntity.setDeleted(false);
        UserEntity savedUser = userRepository.save(userEntity);
        return userMapper.toDTO(savedUser);
    }
}