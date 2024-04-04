package usts.paperms.paperms.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import usts.paperms.paperms.entity.Key;
import usts.paperms.paperms.service.KeyService;
import usts.paperms.paperms.service.RSAKeyGenerationService;

@Controller
@RequestMapping("/api/RSA")
public class RSAKeyController {
    @Autowired
    private RSAKeyGenerationService rsaKeyGenerationService;
    @Autowired
    private KeyService keyService;

    @PostMapping("/generateKeys")
    public ResponseEntity<?> generateKeys() throws Exception {
        String keys=rsaKeyGenerationService.custom_generateKeys();
        //按换行符分别获取公钥和私钥
        String[] key=keys.split("\n");
        //将公钥和私钥存入数据库
        Key key1=new Key();
        key1.setKeyName("RSA");
        key1.setKeyPublic(key[0]);
        key1.setKeyPrivate(key[1]);
        //保存到数据库
        keyService.saveKey(key1);
        return ResponseEntity.ok("密钥生成成功");
    }

    @GetMapping("/getKey")
    public ResponseEntity<?> getKey() {
        Key key=keyService.getKey();
        return ResponseEntity.ok(key);
    }

    @GetMapping("/getprivateKey")
    public ResponseEntity<?> getprivateKey() {
        String key=keyService.getprivateKey();
        return ResponseEntity.ok(key);
    }

    @GetMapping("/getpublicKey")
    public ResponseEntity<?> getpublicKey() {
        String key=keyService.getpublicKey();
        return ResponseEntity.ok(key);
    }
}
