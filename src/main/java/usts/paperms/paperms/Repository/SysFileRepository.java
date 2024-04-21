package usts.paperms.paperms.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.SysFile;

@Repository
public interface SysFileRepository extends JpaRepository<SysFile, Long> {
    SysFile findByName(String fileName);

    //按照文件名对文件加密状态进行更新
    @Query(value = "UPDATE `sys_file` SET `is_decrypt` = :decrypt WHERE `name` = :fileName", nativeQuery = true)
    void updateIsDecryptedByFileName(@Param("fileName") String fileName, @Param("decrypt") boolean decrypt);

    Page<SysFile> findByNameContaining(String name, Pageable pageable);

    Page<SysFile> findByCollegeContaining(String classes, Pageable pageable);

    Page<SysFile> findByProducedContaining(String produced, Pageable pageable);

    @Query(value ="SELECT f.*  FROM `sys_file` f  JOIN `sys_check` c ON f.`id` = c.`check_id` WHERE c.`check_status` = :checkStatus and f.`college`=:college", nativeQuery = true)
    Page<SysFile> findFilesByClassCheck(@Param("checkStatus") String checkStatus,@Param("college") String college, Pageable pageable);

    // You can define custom queries if needed
}
