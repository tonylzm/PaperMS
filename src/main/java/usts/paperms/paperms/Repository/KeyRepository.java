package usts.paperms.paperms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.Key;
import usts.paperms.paperms.entity.Users;

import java.util.Optional;

@Repository
public interface KeyRepository  extends JpaRepository<Key, Long> {

    //按照keyName查询
    Key findByKeyName(String keyName);


    @Query(value = "SELECT k.`key_private` FROM `sys_key` k WHERE k.`key_name` = :keyName", nativeQuery = true)
    String findPrivateKeyByKeyName(@Param("keyName") String keyName);

    @Query(value = "SELECT k.`key_public` FROM `sys_key` k WHERE k.`key_name` = :keyName", nativeQuery = true)
    String findPublicKeyByKeyName(@Param("keyName") String keyName);


    Optional<Key> findByUsers(Users user);
}
