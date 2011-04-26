/**
 * Copyright 2011 Security Compass
 * */

package com.securitycompass.labs.falsesecuremobile;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

/**
 * Suite of tools for encryption and decryption.
 * @author Ewan Sinclair
 */
public class CryptoTool {

    private static final String CRYPTO_SPEC = "AES/CBC/PKCS5Padding";
    private final static int KEY_BITS = 256;
    private final static int IV_BYTES = 16;
    private final static int SALT_BYTES = 32;
    private final static int NUM_ITERATIONS = 1000;

    /** Arbitrary key */
    public static final String DEFAULT_B64_KEY_STRING = "T0xXpDs1lT9q36aPehvDnaX3EgaFlM4JKIGYvqTqld0=";

    /** No-argument constuctor. Doesn't do anything.  */
    public CryptoTool() {

    }

    /**
     * Encrypts the provided String and returns a base64 representation.
     * @param cleartext The text to be encrypted.
     * @param key The AES key to use for encrypting.
     * @param iv The initialisation vector to use for encrypting.
     * @return A Base64 encoded String representing the encrypted text.
     * @throws InvalidKeyException if the given key can't be used
     * @throws NoSuchPaddingException if our required padding type isn't available
     * @throws NoSuchAlgorithmException if the encryption algorithm isn't available.
     * @throws BadPaddingException if the padding was bad
     * @throws IllegalBlockSizeException if the cipher block is of an illegal size
     * @throws InvalidAlgorithmParameterException if the algorithm has had parameters set 
     */
    public String encryptToB64String(String cleartext, byte[] key, byte[] iv) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        String ciphertext = "";

        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance(CRYPTO_SPEC);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] ciphertextBytes = cipher.doFinal(cleartext.getBytes());
        ciphertext = new String(Base64.encode(ciphertextBytes, Base64.DEFAULT));

        return ciphertext;
    }

    /**
     * Encrypts the provided byte array.
     * @param cleartext The text to be encrypted.
     * @param key The AES key to use for encrypting.
     * @param iv The initialisation vector to use for encrypting.
     * @return A byte array representing the encrypted text.
     * @throws InvalidKeyException if the given key can't be used
     * @throws NoSuchPaddingException if our required padding type isn't available
     * @throws NoSuchAlgorithmException if the encryption algorithm isn't available.
     * @throws BadPaddingException if the padding was bad
     * @throws IllegalBlockSizeException if the cipher block is of an illegal size
     * @throws InvalidAlgorithmParameterException if the algorithm has had parameters set 
     */
    public byte[] encrypt(byte[] cleartext, byte[] key, byte[] iv) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance(CRYPTO_SPEC);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] ciphertextBytes = cipher.doFinal(cleartext);

        return ciphertextBytes;
    }
    
    /**
     * Decrypts the provided base64 String and returns plaintext.
     * @param ciphertextB64 The text to be decrypted.
     * @param key The AES key to use for decrypting.
     * @param iv The initialisation vector to use for decrypting.
     * @return A String representation of the cleartext.
     * @throws InvalidKeyException if the given key can't be used
     * @throws NoSuchPaddingException if our required padding type isn't available
     * @throws NoSuchAlgorithmException if the encryption algorithm isn't available.
     * @throws BadPaddingException if the padding was bad
     * @throws IllegalBlockSizeException if the cipher block is of an illegal size
     * @throws InvalidAlgorithmParameterException if the algorithm has had parameters set 
     */
    public String decryptB64String(String ciphertextB64, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] ciphertextBytes = Base64.decode(ciphertextB64, Base64.DEFAULT);

        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(CRYPTO_SPEC);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] cleartextBytes = cipher.doFinal(ciphertextBytes);
        String cleartext = new String(cleartextBytes);

        return cleartext;
    }

    /**
     * Decrypts the provided byte array and returns plaintext.
     * @param ciphertextBytes The text to be decrypted.
     * @param key The AES key to use for decrypting.
     * @param iv The initialisation vector to use for decrypting.
     * @return A Base64 encoded String representing the encrypted text.
     * @throws InvalidKeyException if the given key can't be used
     * @throws NoSuchPaddingException if our required padding type isn't available
     * @throws NoSuchAlgorithmException if the encryption algorithm isn't available.
     * @throws BadPaddingException if the padding was bad
     * @throws IllegalBlockSizeException if the cipher block is of an illegal size
     * @throws InvalidAlgorithmParameterException if the algorithm has had parameters set 
     */
    public String decryptBytes(byte[] ciphertextBytes, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(CRYPTO_SPEC);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] cleartextBytes = cipher.doFinal(ciphertextBytes);
        String cleartext = new String(cleartextBytes);

        return cleartext;
    }
    
    /**
     * Generates a random AES key. 
     * @return A randomly generated key.
     * @throws NoSuchAlgorithmException if the AES algorithm is unavailable.
     */
    public SecretKey genRandomKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_BITS);
        SecretKey key = keyGen.generateKey();
        return key;
    }

    /**
     * Generates a random AES key, encoded as a Base64 String.
     * @return A randomly generated key, encoded as a Base64 String.
     * @throws NoSuchAlgorithmException if the AES algorithm is unavailable.
     */
    public String genRandomBase64KeyString() throws NoSuchAlgorithmException {
        SecretKey key = genRandomKey();
        byte[] keyBytes = key.getEncoded();
        String b64String = new String(Base64.encode(keyBytes, Base64.DEFAULT));
        return b64String;
    }

    /**
     * @param password The password to generate the key from.
     * @param salt Some bytes to salt the password with.
     * @param iterations How many iterations to use when generating the key.
     * @return A key produced from the given password and parameters.
     * @throws NoSuchAlgorithmException if the PBKDF2 algorithm was unavailable.
     * @throws InvalidKeySpecException if the keyspec produced from the given parameters is rejected.
     */
    public SecretKey genKeyPwkdf2(String password, byte[] salt, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
        SecretKey generatedKey = f.generateSecret(keySpec);
        return generatedKey;
    }

    /**
     * Generates a random initialisation vector.
     * @return A random initialisation vector.
     */
    public byte[] getIv(){
        SecureRandom sr = new SecureRandom();
        byte[] iv = new byte[IV_BYTES];
        sr.nextBytes(iv);
        return iv;
    }
    
    /** Decodes Base64 encoded Strings.
     * @param b64String The base64 String to be decoded.
     * @return The decoded String.
     */
    public byte[] decodeB64(String b64String) {
        return Base64.decode(b64String, Base64.DEFAULT);
    }
}