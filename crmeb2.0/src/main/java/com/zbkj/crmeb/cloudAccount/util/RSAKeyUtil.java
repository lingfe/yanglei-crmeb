package com.zbkj.crmeb.cloudAccount.util;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

// 生成RSA.KEY
public class RSAKeyUtil {

    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        //System.out.println("公钥："+new BASE64Encoder().encodeBuffer(publicKey.getEncoded()));
        //System.out.println("私钥："+new BASE64Encoder().encodeBuffer(privateKey.getEncoded()));
    }

}
