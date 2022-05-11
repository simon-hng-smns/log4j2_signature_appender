package example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Example {

    public static void main(String[] args) {

        PrintStream prevConsole = System.out;

        ByteArrayOutputStream console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console));

        Logger logger = LogManager.getLogger();
        logger.info("first log");
        logger.info("second log");
        logger.info("third log");

        List<VerifiedLogEvent> logEvents = Arrays
                .stream(console.toString().split("((?<=(\\r\\n)))"))
                .map(VerifiedLogEvent::createVerifiedLogEvent)
                .collect(Collectors.toList());

        PublicKey publicKey = getPublicKey("receiverKeystore.p12", "password");

        for (int i = 0; i < logEvents.size(); i++) {
            byte[] lastSignature = i == 0 ? new byte[0] : logEvents.get(i - 1).getSignature();
            VerifiedLogEvent currentLogEvent = logEvents.get(i);

            currentLogEvent.setLastSignature(lastSignature);
            currentLogEvent.verify(publicKey);
        }

        System.setOut(prevConsole);

        logEvents.forEach(System.out::println);
    }

    private static PublicKey getPublicKey(String path, String password) {

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            URL resource = Example.class.getClassLoader().getResource(path);

            keyStore.load(new FileInputStream(
                    Paths.get(resource.toURI()).toFile()
            ), password.toCharArray());

            Certificate certificate = keyStore.getCertificate("signatureKeyPair");
            return certificate.getPublicKey();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
