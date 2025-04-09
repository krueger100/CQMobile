package com.example.cq_mobile.LoginFolder;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Arrays;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;

public class EncryptionUtil {

    private static final String KEY_ALIAS = "MyKeyAlias";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_STORE = "AndroidKeyStore";

    private static KeyStore keyStore;

    static {
        try {
            keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generate a new encryption key and store it in Keystore
    public static SecretKey generateKey() throws Exception {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        } else {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE);
            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build();
            keyGenerator.init(keyGenParameterSpec);
            return keyGenerator.generateKey();
        }
    }

    // Encrypt data using AES GCM
    public static String encrypt(String data) throws Exception {
        SecretKey key = generateKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] encryption = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedData = new byte[iv.length + encryption.length];
        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
        System.arraycopy(encryption, 0, encryptedData, iv.length, encryption.length);

        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    // Decrypt data using AES GCM
    public static String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            Log.e("EncryptionUtil", "Encrypted data is null or empty");
            return null;
        }

        try {
            byte[] encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
            if (encryptedBytes.length < 12) {
                Log.e("EncryptionUtil", "Encrypted data is too short. Expected at least 12 bytes for IV.");
                return null;
            }

            byte[] iv = Arrays.copyOfRange(encryptedBytes, 0, 12);
            byte[] cipherText = Arrays.copyOfRange(encryptedBytes, 12, encryptedBytes.length);

            SecretKey key = generateKey();
            if (key == null) {
                Log.e("EncryptionUtil", "Failed to retrieve decryption key.");
                return null;
            }

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);  // 128-bit authentication tag length
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] decryptedData = cipher.doFinal(cipherText);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e("EncryptionUtil", "Decryption failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
