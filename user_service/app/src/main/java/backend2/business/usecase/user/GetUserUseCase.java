package backend2.business.usecase.user;

import backend2.domain.UserDTO;
import backend2.persistence.UserRepository;
import backend2.business.mapper.UserMapper;
import backend2.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO getUser(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    public UserDTO getUserByUsernameIfNotDeleted(String username) {
        return userRepository.findByUsernameAndDeletedFalse(username)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or is deleted: " + username));
    }
}