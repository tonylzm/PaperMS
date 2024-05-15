package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import usts.paperms.paperms.Repository.HistoryChecked;
import usts.paperms.paperms.entity.historychecked;

@Service
public class HistoryFileService {
    @Autowired
    private HistoryChecked historyChecked;

    //返回分页查找的历史文件
    public Page<historychecked> findPageByProducedAndName(Integer pageNum, Integer pageSize, String produced, String name) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return historyChecked.findByProducedContainingAndNameContaining(produced, name, pageable);
    }

    public void saveHistoryFile(historychecked historychecked) {
        historyChecked.save(historychecked);
    }

}
