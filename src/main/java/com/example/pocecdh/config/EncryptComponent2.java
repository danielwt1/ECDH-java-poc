package com.example.pocecdh.config;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class EncryptComponent2 {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public EncryptComponent2() {
    }

    public KeyPair getKeyPair() {
        System.out.println("Java generate an EC keypair");
        String ecdhCurvenameString = "secp384r1";
        // standard curvennames
        // secp256r1 [NIST P-256, X9.62 prime256v1]
        // secp384r1 [NIST P-384]
        // secp521r1 [NIST P-521]
        KeyPairGenerator keyPairGenerator = null;
        KeyPair ecdhKeyPair = null;
        try {
            ECGenParameterSpec ecParameterSpec = new ECGenParameterSpec(ecdhCurvenameString);
            keyPairGenerator = KeyPairGenerator.getInstance("EC"/*, "SunEC" */);
            keyPairGenerator.initialize(ecParameterSpec);
            ecdhKeyPair = keyPairGenerator.genKeyPair();
            //privateKey = ecdhKeyPair.getPrivate();
            //publicKey = ecdhKeyPair.getPublic();
            //System.out.println("privateKey: " +privateKeyToString( privateKey));
            //System.out.println("publicKey: " +publicKeyToString( publicKey));
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e /* | NoSuchProviderException e*/) {
            throw new RuntimeException(e);
        }
        return ecdhKeyPair;
    }


    protected String encrypt(String plainText,byte[] shared, int random) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
        SecretKey secretKey = new SecretKeySpec(shared, random, 32, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        byte[] randomN = new byte[1];
        randomN[0] = (byte) random;
        byte[] cipherTextWithIv = new byte[iv.length + cipherText.length + randomN.length];
        //System.arraycopy(iv, 0, cipherTextWithIv, 0, iv.length);
        //System.arraycopy(cipherText, 0, cipherTextWithIv, iv.length, cipherText.length);
        //System.arraycopy(iv, 0, cipherTextWithIv, 0, iv.length);
        System.arraycopy(iv, 0, cipherTextWithIv, 0, iv.length);
        System.arraycopy(randomN, 0, cipherTextWithIv, 12, randomN.length);

        System.arraycopy(cipherText, 0, cipherTextWithIv, 13, cipherText.length);
        System.out.println(Base64.getEncoder().encodeToString(cipherTextWithIv));
        return Base64.getEncoder().encodeToString(cipherTextWithIv);
    }


    protected String decrypt(String cipherText, byte[] shared) throws Exception {
        byte[] cipherTextBytes = Base64.getDecoder().decode(cipherText);

        byte[] iv = new byte[12];
        byte[] randomN = new byte[1];


        System.arraycopy(cipherTextBytes,0, iv, 0, 12);
        System.arraycopy(cipherTextBytes, 12, randomN, 0, 1);
        SecretKey secretKey = new SecretKeySpec(shared,randomN[0], 32, "AES");
        System.out.println(this.sharedKeyToString(secretKey));
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        byte[] plainTextBytes = cipher.doFinal(cipherTextBytes, 13, cipherTextBytes.length - 13);


        return new String(plainTextBytes);
    }

    protected String sharedKeyToString(SecretKey secretKey) {
        byte[] byteKey = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(byteKey);
    }

    protected PublicKey stringToPublicKey(String pubKey) throws Exception {
        String pubString = pubKey.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace("\n", "").trim();
        byte[] byteKey = Base64.getDecoder().decode(pubString.replaceAll(" ", ""));
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory kf = KeyFactory.getInstance("EC");

        return kf.generatePublic(X509publicKey);
    }

    protected String privateKeyToString(PrivateKey priv) {
        byte[] bytePriv = priv.getEncoded();
        return Base64.getEncoder().encodeToString(bytePriv);
    }

    public String publicKeyToString(PublicKey publicKey) {
        byte[] bytePriv = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytePriv);
    }

    protected SecretKey stringToSharedKey(String sharedKey) {
        byte[] byteKey = Base64.getDecoder().decode(sharedKey.getBytes());
        return new SecretKeySpec(byteKey, 0, byteKey.length, "AES"); // AES, DES, Blowfish, ...
    }

    protected byte[] sharedSecret(PrivateKey privateKey, PublicKey publicKey) {
        // Calcula el secreto compartido
        KeyAgreement keyAgreement = null;
        try {
            keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] sharedSecret = keyAgreement.generateSecret();
        return sharedSecret;
    }


    public PrivateKey getPrivateKey() {
        return privateKey;
    }


}