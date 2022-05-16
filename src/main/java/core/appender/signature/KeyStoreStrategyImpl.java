package core.appender.signature;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Objects;

public class KeyStoreStrategyImpl implements KeyStoreStrategy {

    KeyStore keyStore;

    String password;

    KeyStoreStrategyImpl(String path, String password) {

        this.password = password;

        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            URL resource = getClass().getClassLoader().getResource(path);
            keyStore.load(new FileInputStream(
                    Paths.get(Objects.requireNonNull(resource).toURI()).toFile()
            ), password.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        } catch (IOException | CertificateException | URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override public PrivateKey getPrivateKey() {

        try {
            return (PrivateKey) keyStore.getKey("signatureKeyPair", password.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        } catch (UnrecoverableKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
