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
import java.util.Objects;
import java.util.stream.Collectors;

public class Example {

    private static String writeLogs() {

        PrintStream prevConsole = System.out;

        ByteArrayOutputStream console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console));

        Logger logger = LogManager.getLogger();
        logger.info("first log");
        logger.info("second log");
        logger.info("third log");

        System.setOut(prevConsole);
        return console.toString();
    }

    private static boolean verifyLogs(String input) {

        List<LogEventVerifier> logEvents = Arrays
                .stream(input.split("((?<=(\\r\\n)))"))
                .map(LogEventVerifier::createLogEventVerifier)
                .collect(Collectors.toList());

        for (int i = 0; i < logEvents.size(); i++) {
            byte[] lastSignature = i == 0 ? new byte[0] : logEvents.get(i - 1).getSignature();
            LogEventVerifier currentLogEvent = logEvents.get(i);

            currentLogEvent.setLastSignature(lastSignature);
        }

        PublicKey publicKey = getPublicKey();
        logEvents.forEach(event -> event.verify(publicKey));

        return logEvents.stream().map(e -> e.verified)
                .reduce(true, (a, b) -> a && b);
    }

    public static void main(String[] args) {

        String consoleInput = writeLogs();

        boolean logsVerified = verifyLogs(consoleInput);

        System.out.println(logsVerified ? "Successfully verified logs" : "Failed to verify logs");
    }

    private static PublicKey getPublicKey() {

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            URL resource = Example.class.getClassLoader().getResource("receiverKeystore.p12");

            keyStore.load(new FileInputStream(
                    Paths.get(Objects.requireNonNull(resource).toURI()).toFile()
            ), "password".toCharArray());

            Certificate certificate = keyStore.getCertificate("signatureKeyPair");
            return certificate.getPublicKey();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
