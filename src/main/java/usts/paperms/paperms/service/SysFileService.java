package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import usts.paperms.paperms.Repository.CheckRespository;
import usts.paperms.paperms.Repository.SysFileRepository;
import usts.paperms.paperms.entity.Check;
import usts.paperms.paperms.entity.SysFile;
import usts.paperms.paperms.service.SecurityService.RSAFileEncryptionService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class SysFileService {
    private static final String ENCRYPTED_FILE_DIRECTORY = "src/main/resources/static/files/";
    @Autowired
    private SysFileRepository sysFileRepository;
    @Autowired
    private CheckRespository checkRespository;
    @Autowired
    private RSAFileEncryptionService rsaFileEncryptionService;

    public SysFile save(SysFile sysFile) {

        SysFile savedfile = sysFileRepository.save(sysFile);
        //check表中进行关联
        //如果已经存在文件，则check表中不再添加
        if(checkRespository.findBySysFile(savedfile).isPresent()) {
            return savedfile;
        }
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

    public Page<SysFile> findPageByProduced(Integer pageNum, Integer pageSize, String produced, String name) {
        // 构建分页请求对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 调用 Spring Data JPA 的方法执行分页查询
        return sysFileRepository.findByProducedContainingAndNameContaining(produced,name, pageable);
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
    //如果status为系主任通过，则将文件再次加密，交由院长审核
    public void updateClassCheckByFileName(String fileName,String classCheck,String opinion,String status) throws Exception {
        SysFile sysFile = sysFileRepository.findByName(fileName);
        Optional<Check> checkOptional = checkRespository.findBySysFile(sysFile);
        //如果status为系主任通过，则将文件再次加密
        if(status.equals("系主任通过")) {
            //加密文件
            // 构建文件路径
            String filePath = ENCRYPTED_FILE_DIRECTORY + fileName;
            // 检查文件是否存在
            if (!Files.exists(Paths.get(filePath))) {
                return;
            }
            // 读取文件内容转化为MultipartFile类型
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            //文件加密
            rsaFileEncryptionService.encryptFiles(fileContent,fileName);
            //将文件解密状态设置为false
            sysFile.setDecrypt(false);
            sysFileRepository.save(sysFile);
        }
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

}
