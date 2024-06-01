package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usts.paperms.paperms.common.MinIoUtil;
import usts.paperms.paperms.config.MinIoProperties;
import usts.paperms.paperms.entity.pigeonhole;
import usts.paperms.paperms.Repository.pigeonholeRespository;

@Service
public class pigeonholeService {
    @Autowired
    private pigeonholeRespository pigeonholeRespository;
    @Autowired
    MinIoProperties minIoProperties;

    public void save(pigeonhole pigeonhole) {
        pigeonholeRespository.save(pigeonhole);
    }

    public pigeonhole findByNmae(String name) {
        return pigeonholeRespository.findByName(name);
    }

    public Page<pigeonhole> findAllByCollege(String name,String college, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return pigeonholeRespository.findAllByNameContainingAndCollege(name,college, pageable);
    }

    public String findMD5ByFileName(String fileName) {
        pigeonhole pigeonhole = pigeonholeRespository.findByName(fileName);
        return pigeonhole != null ? pigeonhole.getMd5() : null;
    }


    @Transactional
    public void delet(String fileName){
        MinIoUtil.deleteFile("pigeonhole",fileName);
        pigeonholeRespository.updateIsDeletedByFileName(fileName,true);
    }
    @Transactional
    public void updateDecrypted(String fileName, boolean decrypted) {
        pigeonholeRespository.updateIsDecryptedByFileName(fileName, decrypted);
    }
    @Transactional
    public void delete(String fileName) {
        pigeonholeRespository.deleteByName(fileName);
    }

    public Boolean findDecryptedByFileName(String fileName) {
        pigeonhole pigeonhole = pigeonholeRespository.findByName(fileName);
        return pigeonhole.isDecrypt();
    }


}
