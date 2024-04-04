package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usts.paperms.paperms.Repository.KeyRepository;
import usts.paperms.paperms.entity.Key;

@Service
public class KeyService {
    @Autowired
    private KeyRepository keyRepository;

    public void saveKey(Key key1) {
        keyRepository.save(key1);
    }

    public Key getKey() {
        return keyRepository.findByKeyName("RSA");
    }

    public String getprivateKey() {

        return keyRepository.findPrivateKeyByKeyName("RSA");
    }

    public String getpublicKey() {
        return keyRepository.findPublicKeyByKeyName("RSA");

    }
}
