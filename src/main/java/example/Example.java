package example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Example {

    public static void main(String[] args) {

        PrintStream prevConsole = System.out;

        ByteArrayOutputStream console = new ByteArrayOutputStream();
        //System.setOut(new PrintStream(console));

        Logger logger = LogManager.getLogger();
        logger.info("first log");
        logger.info("second log");
        logger.info("third log");

/*
        List<VerifiedLogEvent> logEvents = Arrays
                .stream(console.toString().split("((?<=(\\r\\n)))"))
                .map(VerifiedLogEvent::createVerifiedLogEvent)
                .collect(Collectors.toList());

        Key key = new SecretKeyProviderExample().getSecretKey();

        for (int i = 0; i < logEvents.size(); i++) {
            byte[] lastSignature = i == 0 ? new byte[0] : logEvents.get(i - 1).getSignature();
            VerifiedLogEvent currentLogEvent = logEvents.get(i);

            currentLogEvent.setLastSignature(lastSignature);
            currentLogEvent.verify(key);
        }

        System.setOut(prevConsole);

        logEvents.forEach(System.out::println);
*/
    }
}
