package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import usts.paperms.paperms.Repository.CheckRespository;
import usts.paperms.paperms.Repository.SysFileRepository;
import usts.paperms.paperms.entity.Check;
import usts.paperms.paperms.entity.SysFile;

import java.util.Optional;

@Service
public class SysFileService {
    @Autowired
    private SysFileRepository sysFileRepository;
    @Autowired
    private CheckRespository checkRespository;

    public SysFile save(SysFile sysFile) {

        SysFile savedfile = sysFileRepository.save(sysFile);
        //check表中进行关联
        Check check = new Check();
        check.setSysFile(sysFile);
        check.setCheckStatus("未审核");
        checkRespository.save(check);

        return savedfile;
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

    public Page<SysFile> findPageByCollege(Integer pageNum, Integer pageSize, String college) {
        // 构建分页请求对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 调用 Spring Data JPA 的方法执行分页查询
        return sysFileRepository.findByCollegeContaining(college, pageable);
    }

    public Page<SysFile> findPageByProduced(Integer pageNum, Integer pageSize, String produced) {
        // 构建分页请求对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 调用 Spring Data JPA 的方法执行分页查询
        return sysFileRepository.findByProducedContaining(produced, pageable);
    }

    //通过文件名称查找文件审核状态
    public Optional<String> findCheckByFileName(String fileName) {
        Optional<SysFile> sysFileOptional = Optional.ofNullable(sysFileRepository.findByName(fileName));
        if(sysFileOptional.isPresent()) {
            SysFile sysFile = sysFileOptional.get();
            Optional<Check> checkOptional = checkRespository.findBySysFile(sysFile);
            if (checkOptional.isPresent()) {
                Check check = checkOptional.get();
                //返回check的值
                return Optional.of(check.getCheckStatus());
            }
        }
        return Optional.empty();
    }

    //通过文件查找文件审核不通过原因
    public Optional<String> findOptionByFileName(String fileName) {
        Optional<SysFile> sysFileOptional = Optional.ofNullable(sysFileRepository.findByName(fileName));
        if(sysFileOptional.isPresent()) {
            SysFile sysFile = sysFileOptional.get();
            Optional<Check> checkOptional = checkRespository.findBySysFile(sysFile);
            if (checkOptional.isPresent()) {
                Check check = checkOptional.get();
                //返回check的值
                return Optional.of(check.getOpinion());
            }
        }
        return Optional.empty();
    }
    //通过文件更新classCheck字段
    public void updateClassCheckByFileName(String fileName,String classCheck,String opinion,String status) {
        SysFile sysFile = sysFileRepository.findByName(fileName);
        Optional<Check> checkOptional = checkRespository.findBySysFile(sysFile);
        if(checkOptional.isPresent()) {
            Check check = checkOptional.get();
            check.setClassCheck(classCheck);
            check.setOpinion(opinion);
            check.setCheckStatus(status);
            checkRespository.save(check);
        }
    }

    //通过文件更新collegeCheck审核状态
    public void updateCollegeCheckByFileName(String fileName,String collegeCheck,String opinion,String status) {
        SysFile sysFile = sysFileRepository.findByName(fileName);
        Optional<Check> checkOptional = checkRespository.findBySysFile(sysFile);
        if(checkOptional.isPresent()) {
            Check check = checkOptional.get();
            check.setCollegeCheck(collegeCheck);
            check.setOpinion(opinion);
            check.setCheckStatus(status);
            checkRespository.save(check);
        }
    }

    //分页查找classCheck通过的文件，两个表关联查询
    public Page<SysFile> findPageByClassCheck(Integer pageNum, Integer pageSize, String status,String college) {
        // 构建分页请求对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 调用 Spring Data JPA 的方法执行分页查询
        return sysFileRepository.findFilesByClassCheck(status, college,pageable);
    }

    public SysFile findByName(String fileName) {
        return sysFileRepository.findByName(fileName);
    }

    // Other methods as needed (e.g., findById, findAll, delete, etc.)
}
