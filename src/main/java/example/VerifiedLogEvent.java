package example;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Arrays;

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

    public void verify(Key key) {

        SignStrategyExample signStrategy = new SignStrategyExample();

        byte[] decryptedSignature = signStrategy.decryptSignature(signature, key);

        ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
        try {
            msgStream.write(message);
            msgStream.write(lastSignature);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] msgHash = signStrategy.getHash(msgStream.toByteArray());

        this.verified = Arrays.equals(decryptedSignature, msgHash);
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
