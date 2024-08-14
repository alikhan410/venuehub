package com.venuehub.authservice.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

/**
 * Utility class for generating RSA key pairs.
 * <p>
 * This class provides a method to generate an RSA key pair with a key size of 2048 bits.
 * The generated public and private keys are encoded in Base64 and printed to the console.
 * </p>
 */
public class KeyPairUtility {

    /**
     * Generates an RSA key pair with a key size of 2048 bits.
     * <p>
     * This method creates a new instance of {@link KeyPairGenerator} for the RSA algorithm,
     * initializes it with a key size of 2048 bits, and generates a key pair.
     * The public and private keys are encoded in Base64 and printed to the console.
     * </p>
     *
     * @return the generated {@link KeyPair}.
     * @throws IllegalStateException if there is an error during key pair generation.
     */
    public static KeyPair generate() throws IllegalStateException {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
            String publicKeyStr = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKeyStr = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            System.out.println("Public Key: " + publicKeyStr);
            System.out.println("Private Key: " + privateKeyStr);
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}
