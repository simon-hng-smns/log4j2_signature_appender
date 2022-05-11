package core.appender.signature;

import java.security.*;

public class SignStrategyImpl implements SignStrategy {

    Signature signature;

    SignStrategyImpl(String algorithm, PrivateKey privateKey) {

        try {
            signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override public byte[] sign(byte[] data) {

        try {
            signature.update(data);
            return signature.sign();
        } catch (SignatureException e) {
            throw new UnsupportedOperationException();
        }

    }
}
