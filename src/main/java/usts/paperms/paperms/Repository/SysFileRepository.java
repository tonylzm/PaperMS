package usts.paperms.paperms.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.SysFile;

@Repository
public interface SysFileRepository extends JpaRepository<SysFile, Long> {
    SysFile findByName(String fileName);

    Page<SysFile> findByNameContaining(String name, Pageable pageable);

    Page<SysFile> findByCollegeContaining(String classes, Pageable pageable);

    Page<SysFile> findByProducedContaining(String produced, Pageable pageable);
    // You can define custom queries if needed
}
