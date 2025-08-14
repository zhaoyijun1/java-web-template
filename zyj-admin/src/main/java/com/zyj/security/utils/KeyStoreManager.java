package com.zyj.security.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * 密钥库管理，测试用
 *
 * @author zyj
 * @version 1.0.0
 */
public class KeyStoreManager {

    // 密钥库文件路径
    private static final String KEYSTORE_PATH = "keystore.p12";
    // 密钥库类型
    private static final String KEYSTORE_TYPE = "PKCS12";
    // 密钥库密码
    private static final String KEYSTORE_PASSWORD = "5jE1wBittyT1queueHxnMpNKyYHCCSYQlO7lvUuiQwkA";
    // 密钥别名
    private static final String KEY_ALIAS = "myRSAKey";
    // 密钥密码
    private static final String KEY_PASSWORD = "123456";

    /**
     * 创建或加载密钥库
     */
    public static KeyStore loadKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);

        // 检查密钥库文件是否存在
        try (FileInputStream fis = new FileInputStream(KEYSTORE_PATH)) {
            keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray());
        } catch (Exception e) {
            // 如果不存在，创建新的密钥库
            keyStore.load(null, null);
        }
        return keyStore;
    }

    /**
     * 创建自签名证书
     */
    public static Certificate createSelfSignedCert() throws Exception {
        // 自签名证书的内容
        String certInfo = """
                -----BEGIN CERTIFICATE-----
                这里是Base64编码的证书内容
                -----END CERTIFICATE-----
                """;
        return CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(certInfo.getBytes()));
    }

    /**
     * 保存密钥对到密钥库
     */
    public static void saveToKeyStore(Certificate[] certChain) throws Exception {
        KeyStore keyStore = loadKeyStore();

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            // 获取密钥对
            KeyPair keyPair = SecretUtil.generateRsaKey();

            // 将密钥对存入密钥库
            keyStore.setKeyEntry(KEY_ALIAS, keyPair.getPrivate(), KEY_PASSWORD.toCharArray(), certChain);

            // 保存密钥库到文件
            try (FileOutputStream fos = new FileOutputStream(KEYSTORE_PATH)) {
                keyStore.store(fos, KEYSTORE_PASSWORD.toCharArray());
            }
            System.out.println("RSA 密钥对已生成并保存到密钥库");
        } else {
            System.out.println("密钥别名已存在");
        }
    }

    /**
     * 从密钥库中获取私钥
     */
    public static PrivateKey getPrivateKey() throws Exception {
        KeyStore keyStore = loadKeyStore();
        // 检查别名是否存在
        if (keyStore.containsAlias(KEY_ALIAS)) {
            // 获取私钥
            return (PrivateKey) keyStore.getKey(KEY_ALIAS, KEY_PASSWORD.toCharArray());

        } else {
            System.out.println("密钥别名不存在");
        }
        return null;
    }

    /**
     * 从密钥库中获取公钥
     */
    public static PublicKey getPublicKey() throws Exception {
        KeyStore keyStore = loadKeyStore();
        // 检查别名是否存在
        if (keyStore.containsAlias(KEY_ALIAS)) {
            // 获取公钥
            return keyStore.getCertificate(KEY_ALIAS).getPublicKey();
        } else {
            System.out.println("密钥别名不存在");
        }
        return null;
    }

}
