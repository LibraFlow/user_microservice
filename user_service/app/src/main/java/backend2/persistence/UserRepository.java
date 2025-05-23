package backend2.persistence;

import backend2.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByUsernameAndDeletedFalse(String username);
    List<UserEntity> findByDeletedTrueAndDeletedAtBefore(LocalDate date);

}
