package backend2.business.user;

import backend2.persistence.UserRepository;
import backend2.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import backend2.security.EncryptionService;
import backend2.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCase {

    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public Void deleteUser(Integer id) {
        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Anonymize user data
        user.setUsername("deleted_user_" + id);
        user.setEmail(encryptionService.encrypt("deleted_" + id + "@deleted.com"));
        user.setAddress(encryptionService.encrypt("DELETED"));
        user.setPhone(encryptionService.encrypt("DELETED"));
        user.setDeleted(true);
        user.setDeletedAt(LocalDate.now());
        
        userRepository.save(user);
        return null;
    }
}