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

/**
 * Component that holds RSA public and private keys for authentication.
 * <p>
 * This class can be configured to either generate a new key pair on startup
 * or to use existing keys provided through environment variables.
 * </p>
 */
@Component
@Getter
public class RSAKeyProperties {
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

//    /**
//     * Default constructor that generates a new RSA key pair on each startup.
//     * <p>
//     * This constructor uses the {@link KeyPairUtility#generate()} method to create a new
//     * RSA key pair. The generated keys are assigned to the {@link #publicKey} and
//     * {@link #privateKey} fields.
//     * </p>
//     */
//    public RSAKeyProperties() {
//        KeyPair keyPair = KeyPairUtility.generate();
//        this.publicKey = (RSAPublicKey) keyPair.getPublic();
//        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
//    }

    /**
     * Constructs an instance using the provided RSA public and private key strings.
     * <p>
     * This constructor initializes the RSA keys from Base64-encoded key strings. It expects
     * the keys to be provided as environment variables, which are decoded and converted
     * to {@link RSAPublicKey} and {@link RSAPrivateKey} objects.
     * </p>
     *
     * @param publicKeyStr the Base64-encoded RSA public key.
     * @param privateKeyStr the Base64-encoded RSA private key.
     * @throws IllegalStateException if the provided key strings are null or empty.
     * @throws Exception if there is an error decoding the key strings or generating the keys.
     */
    public RSAKeyProperties(@Value("${jwt.publicKey}") String publicKeyStr,
                            @Value("${jwt.privateKey}") String privateKeyStr) throws Exception {

        if (publicKeyStr == null || publicKeyStr.isEmpty() || privateKeyStr == null || privateKeyStr.isEmpty()) {
            throw new IllegalStateException("Environment variable JWT_PUBLIC_KEY and JWT_PRIVATE_KEY are not set.");
        }

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
    }
}
