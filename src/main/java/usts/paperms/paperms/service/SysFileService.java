package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import usts.paperms.paperms.Repository.SysFileRepository;
import usts.paperms.paperms.entity.SysFile;

@Service
public class SysFileService {
    @Autowired
    private SysFileRepository sysFileRepository;

    public SysFile save(SysFile sysFile) {
        return sysFileRepository.save(sysFile);
    }
    public String findMD5ByFileName(String fileName) {
       SysFile sysFile= sysFileRepository.findByName(fileName);
        return sysFile != null ? sysFile.getMd5() : null;
    }

    public Page<SysFile> findPage(Integer pageNum, Integer pageSize, String name) {
        // 构建分页请求对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 调用 Spring Data JPA 的方法执行分页查询
        return sysFileRepository.findByNameContaining(name, pageable);
    }

    // Other methods as needed (e.g., findById, findAll, delete, etc.)
}
