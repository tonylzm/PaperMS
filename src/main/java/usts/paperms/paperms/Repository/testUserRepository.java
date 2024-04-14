package usts.paperms.paperms.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.Users;

@Repository
public interface testUserRepository extends JpaRepository<Users, Long> {
    // 根据用户名查询用户信息
    Users findByUsername(String username);

    Page<Users> findByUsernameContaining(String username, Pageable pageable);
}