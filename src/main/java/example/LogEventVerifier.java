package example;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;

public class LogEventVerifier {

    boolean verified = false;

    byte[] lastSignature;

    byte[] signature;

    byte[] message;

    LogEventVerifier(byte[] lastSignature, byte[] signature, byte[] message) {

        this.lastSignature = lastSignature;
        this.signature = signature;
        this.message = message;
    }

    public void setLastSignature(byte[] lastSignature) {

        this.lastSignature = lastSignature;
    }

    /**
     * Expectes a log to have the pattern [%signature]%m%n
     *
     * @param log A single log entry. In this example a log entry is a single line
     * @returns A pair of type [byte[], byte[]]
     */

    public static LogEventVerifier createLogEventVerifier(String log) {

        String signature = log;
        signature = signature
                .substring(signature.indexOf("[") + 1)
                .substring(0, signature.indexOf("]") - 1);

        byte[] signatureBytes = new byte[0];
        try {
            signatureBytes = Hex.decodeHex(signature);
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        byte[] messageBytes = log.replace(signature, "").getBytes();

        return new LogEventVerifier(null, signatureBytes, messageBytes);
    }

    public byte[] getSignature() {

        return signature;
    }

    public void verify(PublicKey publicKey) {

        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Concats last signature to the end of our formatted message
            try {
                outputStream.write(message);
                outputStream.write(lastSignature);
            } catch (IOException e) {
                e.printStackTrace();
            }

            signature.update(outputStream.toByteArray());

            this.verified = signature.verify(this.signature);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        } catch (SignatureException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override public String toString() {

        return "LogEventVerifier{\n" +
                "\tverified\t\t" + verified + "\n" +
                "\tlastSignature\t" + Hex.encodeHexString(lastSignature) + "\n" +
                "\tsignature\t\t" + Hex.encodeHexString(signature) + "\n" +
                "\tmessage\t\t\t" + new String(message) +
                '}';
    }
}
