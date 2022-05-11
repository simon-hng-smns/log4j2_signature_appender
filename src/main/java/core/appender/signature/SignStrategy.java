package core.appender.signature;

import java.security.PrivateKey;

public interface SignStrategy {

    /**
     * @param data a byte array of the data to be signed
     * @return The hashed byte array
     */
    byte[] sign(byte[] data);
}
