package core.appender.signature;

import javax.crypto.SecretKey;

public interface SignStrategy {

    /**
     * @param data      a byte array of the data to be signed
     * @param secretKey a secret key. Preferably provided by a
     *                  {@link org.apache.logging.log4j.core.util.SecretKeyProvider}
     * @return The hashed byte array
     */
    byte[] sign(byte[] data, SecretKey secretKey);
}
