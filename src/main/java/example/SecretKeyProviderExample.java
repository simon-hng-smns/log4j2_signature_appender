package example;

import org.apache.logging.log4j.core.util.SecretKeyProvider;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SecretKeyProviderExample implements SecretKeyProvider {

    @Override public SecretKey getSecretKey() {

        return new SecretKeySpec("a".repeat(32).getBytes(), "AES");
    }
}
