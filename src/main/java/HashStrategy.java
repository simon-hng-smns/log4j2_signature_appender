import javax.crypto.SecretKey;

public interface HashStrategy {

    /**
     * @param data      a byte array of the data to be signed
     * @param secretKey a secret key. Preferably provided by a
     *                  {@link org.apache.logging.log4j.core.util.SecretKeyProvider}
     * @return The hashed byte array
     */
    byte[] hash(byte[] data, SecretKey secretKey);
}
