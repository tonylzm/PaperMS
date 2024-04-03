package usts.paperms.paperms.service;

import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.security.*;
@Service
public class RSAKeyGenerationService {

    private static final String PUBLIC_KEY_FILE = "src/main/resources/static/files/security/public.der";
    private static final String PRIVATE_KEY_FILE = "src/main/resources/static/files/security/private.der";

    //RSA密钥生成
    public void generateKeys() throws Exception {
        // Generate key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Get public and private keys
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Save public and private keys
        savePublicKey(publicKey);
        savePrivateKey(privateKey);
    }

    private void savePublicKey(PublicKey publicKey) throws Exception {
        // Save public key to file
        String publicKeyFilePath = PUBLIC_KEY_FILE;
        FileOutputStream fos = new FileOutputStream(publicKeyFilePath);
        fos.write(publicKey.getEncoded());
        fos.close();
    }

    private void savePrivateKey(PrivateKey privateKey) throws Exception {
        // Save private key to file
        String privateKeyFilePath = PRIVATE_KEY_FILE;
        FileOutputStream fos = new FileOutputStream(privateKeyFilePath);
        fos.write(privateKey.getEncoded());
        fos.close();
    }
}