package usts.paperms.paperms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.Salt;
import usts.paperms.paperms.entity.Users;

import java.util.Optional;

@Repository
public interface SaltRepository extends JpaRepository<Salt, Long> {
    // Define custom methods if needed
    Optional<Salt> findByUsers(Users users);
}