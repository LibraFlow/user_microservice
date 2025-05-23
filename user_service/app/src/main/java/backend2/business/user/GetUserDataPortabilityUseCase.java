package backend2.business.user;

import backend2.domain.UserDataPortabilityDTO;
import backend2.persistence.UserRepository;
import backend2.persistence.entity.UserEntity;
import backend2.business.mapper.UserDataPortabilityMapper;
import backend2.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserDataPortabilityUseCase {
    private final UserRepository userRepository;
    private final UserDataPortabilityMapper userDataPortabilityMapper;

    public UserDataPortabilityDTO getUserData(Integer userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return userDataPortabilityMapper.toDTO(user);
    }
} 