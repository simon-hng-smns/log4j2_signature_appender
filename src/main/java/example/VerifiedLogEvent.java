package example;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;

public class VerifiedLogEvent {

    boolean verified = false;

    byte[] lastSignature;

    byte[] signature;

    byte[] message;

    VerifiedLogEvent(byte[] lastSignature, byte[] signature, byte[] message) {

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

    public static VerifiedLogEvent createVerifiedLogEvent(String log) {

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

        return createVerifiedLogEvent(signatureBytes, messageBytes);
    }

    public static VerifiedLogEvent createVerifiedLogEvent(byte[] signature, byte[] message) {

        return new VerifiedLogEvent(null, signature, message);
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
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }

    @Override public String toString() {

        return "VerifiedLogEvent{\n" +
                "\tverified\t\t" + verified + "\n" +
                "\tlastSignature\t" + Hex.encodeHexString(lastSignature) + "\n" +
                "\tsignature\t\t" + Hex.encodeHexString(signature) + "\n" +
                "\tmessage\t\t\t" + new String(message) +
                '}';
    }
}
