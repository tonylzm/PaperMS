package usts.paperms.paperms.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.Users;

@Repository
public interface testUserRepository extends JpaRepository<Users, Long> {
    // 根据用户名查询用户信息
    Users findByUsername(String username);

    Page<Users> findByUsernameContaining(String username, Pageable pageable);

    @Query(value ="SELECT f.*  FROM `test_user` f  JOIN `user_role` c ON f.`id` = c.`user_id` WHERE c.`role` = :role and f.`college`=:college AND (:name IS NULL OR f.`username` LIKE %:name%)", nativeQuery = true)
    Page<Users> findUsersByUserRole(@Param("role") String role, @Param("college") String college, @Param("name") String name, Pageable pageable);

    //通过用户名修改密码
    @Query(value = "update test_user set password = :password where username = :username", nativeQuery = true)
    void updatePasswordByUsername(@Param("username") String username, @Param("password") String password);

}