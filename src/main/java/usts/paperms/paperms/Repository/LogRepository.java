package usts.paperms.paperms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
