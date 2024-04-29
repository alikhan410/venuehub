package com.venuehub.bookingservice.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyPairUtility {

    public static KeyPair generate() throws IllegalStateException {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}
