package com.zyj.security.utils;

import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * 密钥生成工具类
 *
 * @author zyj
 * @version 1.0.0
 */
public class SecretUtil {
    private static final Logger log = LoggerFactory.getLogger(SecretUtil.class);

    /**
     * 使用 KeyGenerator 生成 HMAC-SHA 密钥
     *
     * @return 密钥字符串
     */
    public static String generateHmacShaKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param byteLength 字节长度
     * @return 密钥字符串
     */
    public static String generateRandomSecret(int byteLength) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[byteLength];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 使用Keys工具类 生成 JWT 密钥
     *
     * <p>依赖 jjwt-impl</p>
     *
     * @return 密钥字符串
     */
    public static String generateJwtSecret() {
        SecretKey key = Jwts.SIG.HS256.key().build();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 从密码和盐值生成密钥
     *
     * @param password 密码
     * @param salt     盐值
     * @return 密钥字符串
     */
    public static String generateFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "HmacSHA256");
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 生成 RSA（PKCS#8） 密钥
     */
    public static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
        // 1. 创建 KeyPairGenerator 实例并指定算法为 RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        // 2. 初始化密钥长度（通常使用 2048 或 4096 位）
        keyPairGenerator.initialize(2048);

        // 3. 生成密钥对
        return keyPairGenerator.generateKeyPair();
    }

    public static void main(String[] args) {
        String secret;

        // 1
        try {
            secret = generateHmacShaKey();
            System.out.println("generateHmacShaKey: " + secret);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generateHmacShaKey", e);
        }

        // 2
        secret = generateRandomSecret(32);
        System.out.println("generateRandomSecret: " + secret);


        // 3
//        secret = generateJwtSecret();
//        System.out.println("generateJwtSecret: " + secret);

        // 4
        try {
            secret = generateFromPassword("password", "salt");
            System.out.println("generateFromPassword: " + secret);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error generateFromPassword", e);
        }

        // 5
        try {
            KeyPair keyPair = generateRsaKey();
            // 获取公钥和私钥
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // 打印密钥（Base64 编码）
            System.out.println("-----BEGIN PUBLIC KEY-----");
            System.out.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            System.out.println("-----END PUBLIC KEY-----\n");

            System.out.println("-----BEGIN PRIVATE KEY-----");
            System.out.println(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            System.out.println("-----END PRIVATE KEY-----");
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generateRsaKey", e);
        }
    }

}
