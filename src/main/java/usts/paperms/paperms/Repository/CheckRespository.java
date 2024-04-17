package usts.paperms.paperms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.Check;
import usts.paperms.paperms.entity.SysFile;

import java.util.Optional;

@Repository

public interface CheckRespository extends JpaRepository<Check, Long>{


    Optional<Check>findBySysFile(SysFile sysFile);

}
