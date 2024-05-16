package usts.paperms.paperms.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.Log;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    Page<Log> findAll(Pageable pageable);

    List<Log> findAll();
}
