package example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Example {
    public static void main(String[] args) {
        Logger logger = LogManager.getLogger();
        logger.info("test");
    }
}