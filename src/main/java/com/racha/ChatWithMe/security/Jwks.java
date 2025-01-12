package com.racha.ChatWithMe.security;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import java.security.interfaces.RSAPrivateKey;

import com.nimbusds.jose.jwk.RSAKey;

public class Jwks {

    private Jwks() {
    }

    public static RSAKey generateRsa() {
        KeyPair keyPair = KeyGenerator.generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

}
