package core.appender.signature;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

public class KeyStoreStrategyImpl implements KeyStoreStrategy {

    KeyStore keyStore;

    String password;

    KeyStoreStrategyImpl(String path, String password) {

        this.password = password;

        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            URL resource = getClass().getClassLoader().getResource(path);
            keyStore.load(new FileInputStream(
                    Paths.get(resource.toURI()).toFile()
            ), password.toCharArray());
        } catch (KeyStoreException e) {
            throw new UnsupportedOperationException(e);
        } catch (NoSuchAlgorithmException | IOException | CertificateException | URISyntaxException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

    @Override public PrivateKey getPrivateKey() {

        try {
            return (PrivateKey) keyStore.getKey("signatureKeyPair", password.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            // TODO: better exception handling
            throw new RuntimeException(e);
        }
    }
}
