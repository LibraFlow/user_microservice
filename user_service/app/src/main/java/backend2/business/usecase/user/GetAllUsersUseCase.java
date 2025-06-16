package backend2.business.usecase.user;

import backend2.domain.UserDTO;
import backend2.persistence.UserRepository;
import backend2.business.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getAllDeletedUsers() {
        return userRepository.findByDeletedTrue()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserByIdRegardlessOfDeleted(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}