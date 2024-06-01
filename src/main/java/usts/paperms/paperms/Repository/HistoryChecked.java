package usts.paperms.paperms.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.historychecked;

import java.util.List;

@Repository
public interface HistoryChecked extends JpaRepository<historychecked,Long> {
    //依据produced和name查找文件，分页
    Page<historychecked> findByProducedContainingAndNameContaining(String produced, String name, Pageable pageable);

    List<historychecked> findAllByCollege(String college);

}
