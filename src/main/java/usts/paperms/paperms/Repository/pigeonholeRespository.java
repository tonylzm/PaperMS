package usts.paperms.paperms.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.pigeonhole;

@Repository
public interface pigeonholeRespository extends JpaRepository<pigeonhole, Long> {

    pigeonhole findByName(String name);

    Page<pigeonhole> findAllByNameContainingAndCollege(String name,String college, Pageable pageable);

    @Modifying
    @Query(value = "update `pigeonhole` p set p.`is_decrypt` = :isDecrypted where p.`name` = :fileName" , nativeQuery = true)
    void updateIsDecryptedByFileName(@Param("fileName") String fileName, @Param("isDecrypted") boolean isDecrypted);

    @Modifying
    @Query(value = "update `pigeonhole` p set p.`is_delete` = :isDelete where p.`name` = :fileName" , nativeQuery = true)
    void updateIsDeletedByFileName(@Param("fileName") String fileName, @Param("isDelete") boolean isDelete);

   
    void deleteByName(String fileName);
}
