package usts.paperms.paperms.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.SysFile;

import java.util.List;

@Repository
public interface SysFileRepository extends JpaRepository<SysFile, Long> {
    SysFile findByName(String fileName);

    List<SysFile> findAllByCollege(String college);

    //按照文件名对文件加密状态进行更新
    @Query(value = "UPDATE `sys_file` SET `is_decrypt` = :decrypt WHERE `name` = :fileName", nativeQuery = true)
    void updateIsDecryptedByFileName(@Param("fileName") String fileName, @Param("decrypt") boolean decrypt);

    Page<SysFile> findByNameContaining(String name, Pageable pageable);

    Page<SysFile> findByCollegeContaining(String classes, Pageable pageable);

    Page<SysFile> findByProducedAndNameContaining(String produced, String name, Pageable pageable);

    @Query(value ="SELECT f.* FROM `sys_file` f JOIN `sys_check` c ON f.`id` = c.`check_id` WHERE c.`check_status` = :checkStatus AND f.`college` = :college AND f.`produced` != :produced  AND (:name IS NULL OR f.`name` LIKE %:name%)", nativeQuery = true)
    Page<SysFile> findFilesByClassCheck(@Param("checkStatus") String checkStatus, @Param("college") String college, @Param("name") String name,@Param("produced") String produced, Pageable pageable);

    //输出所有文件，并且加上文件审核状态
    @Query(value="SELECT f.*, c.`check_status` FROM `sys_file` f LEFT JOIN `sys_check` c ON f.`id` = c.`check_id` WHERE f.`produced` =:produced AND (:name IS NULL OR f.`name`LIKE %:name%) ", nativeQuery = true)
    Page<SysFile> findAllFilesWithCheckStatus(@Param("produced") String produced,@Param("name") String name, Pageable pageable);

    @Query(value="SELECT f.*, c.`check_status` FROM `sys_file` f LEFT JOIN `sys_check` c ON f.`id` = c.`check_id` WHERE c.`check_status` = :checkStatus AND f.`college` = :college AND c.class_check=:class_check AND (:name IS NULL OR f.`name`LIKE %:name%)", nativeQuery = true)
    Page<SysFile> findAllFilesCheckClasses(@Param("checkStatus") String checkStatus,@Param("college") String college,@Param("name") String name,@Param("class_check") String class_check, Pageable pageable);

    @Query(value="SELECT f.*, c.`check_status` FROM `sys_file` f LEFT JOIN `sys_check` c ON f.`id` = c.`check_id` WHERE c.`check_status` = :checkStatus AND f.`college` = :college AND c.college_check=:college_check AND (:name IS NULL OR f.`name`LIKE %:name%)", nativeQuery = true)
    Page<SysFile> findAllFilesWithCheckCollege(@Param("checkStatus") String checkStatus,@Param("college") String college,@Param("name") String name,@Param("college_check") String college_check, Pageable pageable);

    //更新pigeonhole状态，通过文件名
    @Modifying
    @Query(value = "UPDATE `sys_file` SET `is_pigeonhole` = :pigeonhole WHERE `name` = :fileName", nativeQuery = true)
    void updateIsPigeonholeByName(@Param("fileName") String fileName, @Param("pigeonhole") boolean pigeonhole);
}
