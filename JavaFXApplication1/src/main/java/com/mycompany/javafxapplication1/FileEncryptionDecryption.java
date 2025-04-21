/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

/**
 *
 * @author kawsar
 */
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;


public class FileEncryptionDecryption {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 16;

     public byte[] encryptChunk(byte[] chunk, String password) {
        try {
            // Generate salt
            byte[] salt = generateSalt();

            // Generate secret key from password and salt
            SecretKey secretKey = generateSecretKey(password.toCharArray(), salt);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Encrypt the chunk
            byte[] encryptedBytes = cipher.doFinal(chunk);

            // Combine salt and encrypted data
            byte[] encryptedChunk = new byte[salt.length + encryptedBytes.length];
            System.arraycopy(salt, 0, encryptedChunk, 0, salt.length);
            System.arraycopy(encryptedBytes, 0, encryptedChunk, salt.length, encryptedBytes.length);

            return encryptedChunk;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    
    public void encryptFile(String inputFile, String outputFile, String password) throws Exception {
        byte[] salt = generateSalt();

        SecretKey secretKey = generateSecretKey(password.toCharArray(), salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

        byte[] encryptedBytes = cipher.doFinal(Files.readAllBytes(Paths.get(inputFile)));

        byte[] combinedBytes = new byte[SALT_LENGTH + IV_LENGTH + encryptedBytes.length];
        System.arraycopy(salt, 0, combinedBytes, 0, SALT_LENGTH);
        System.arraycopy(iv, 0, combinedBytes, SALT_LENGTH, IV_LENGTH);
        System.arraycopy(encryptedBytes, 0, combinedBytes, SALT_LENGTH + IV_LENGTH, encryptedBytes.length);

        Files.write(Paths.get(outputFile), combinedBytes);
    }

    public void decryptFile(String inputFile, String outputFile, String password) throws Exception {
        byte[] combinedBytes = Files.readAllBytes(Paths.get(inputFile));

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        byte[] encryptedBytes = new byte[combinedBytes.length - SALT_LENGTH - IV_LENGTH];

        System.arraycopy(combinedBytes, 0, salt, 0, SALT_LENGTH);
        System.arraycopy(combinedBytes, SALT_LENGTH, iv, 0, IV_LENGTH);
        System.arraycopy(combinedBytes, SALT_LENGTH + IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

        SecretKey secretKey = generateSecretKey(password.toCharArray(), salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        Files.write(Paths.get(outputFile), decryptedBytes);
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    private SecretKey generateSecretKey(char[] password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
}

