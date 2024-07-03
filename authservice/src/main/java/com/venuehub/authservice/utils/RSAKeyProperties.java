package com.venuehub.authservice.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Getter
public class RSAKeyProperties {
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    //Use this if you want to create a random key on each startup
//    public RSAKeyProperties() {
//        KeyPair keyPair = KeyPairUtility.generate();
//        this.publicKey = (RSAPublicKey) keyPair.getPublic();
//        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
//    }


    public RSAKeyProperties(@Value("${jwt.publicKey}") String publicKeyStr,
                            @Value("${jwt.privateKey}") String privateKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
    }

}
