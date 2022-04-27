package example;

import core.appender.signature.SignStrategy;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignStrategyExample implements SignStrategy {

    @Override public byte[] sign(byte[] data, SecretKey secretKey) {

        byte[] hash = getHash(data);
        Cipher cipher = getCipher();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(hash);
        } catch (InvalidKeyException e) {
            throw new UnsupportedOperationException(e);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalArgumentException("Failed to hash");
        }
    }

    public byte[] decryptSignature(byte[] data, Key key) {

        Cipher cipher = getCipher();
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException("Failed to decrypt signature");
        }
    }

    public byte[] getHash(byte[] data) {

        MessageDigest messageDigest = getMessageDigest();
        return messageDigest.digest(data);
    }

    private MessageDigest getMessageDigest() {

        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    Cipher getCipher() {

        try {
            return Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        } catch (NoSuchPaddingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
