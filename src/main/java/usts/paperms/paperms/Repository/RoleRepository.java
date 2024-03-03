package usts.paperms.paperms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.UserRole;
import usts.paperms.paperms.entity.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<UserRole, Long> {
    // 根据用户查询角色信息
    Optional<UserRole> findByUsers(Users users);

}