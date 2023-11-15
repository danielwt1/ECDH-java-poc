package com.example.pocecdh.config;

import java.security.KeyPair;

public class SesionDTO {
    private final KeyPair keyPair;
    private final String publicClientKey;

    public SesionDTO(KeyPair keyPair, String publicClientKey) {
        this.keyPair = keyPair;
        this.publicClientKey = publicClientKey;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public String getPublicClientKey() {
        return publicClientKey;
    }
}
