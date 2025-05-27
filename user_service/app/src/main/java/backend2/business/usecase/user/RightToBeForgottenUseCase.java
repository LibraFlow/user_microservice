package backend2.business.usecase.user;

import backend2.persistence.UserRepository;
import backend2.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import backend2.security.EncryptionService;
import backend2.exception.ResourceNotFoundException;
import backend2.security.PasswordEncoderService;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RightToBeForgottenUseCase {

    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final PasswordEncoderService passwordEncoderService;

    @Transactional
    public Void exerciseRightToBeForgotten(Integer id) {
        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Generate random identifier for anonymization
        String randomId = UUID.randomUUID().toString().substring(0, 8);
        
        // Anonymize user data
        user.setUsername("deleted_user_" + randomId);
        user.setEmail(encryptionService.encrypt("deleted_" + randomId + "@deleted.com"));
        user.setAddress(encryptionService.encrypt("DELETED"));
        user.setPhone(encryptionService.encrypt("DELETED"));
        user.setPwd(passwordEncoderService.encode("DELETED_" + randomId + "_" + LocalDate.now()));
        user.setDeleted(true);
        user.setDeletedAt(LocalDate.now());
        
        userRepository.save(user);
        return null;
    }
}