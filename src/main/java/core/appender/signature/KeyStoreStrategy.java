package core.appender.signature;

import java.security.PrivateKey;

public interface KeyStoreStrategy {

    PrivateKey getPrivateKey();
}
