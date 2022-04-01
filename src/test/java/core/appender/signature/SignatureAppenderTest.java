package core.appender.signature;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SignatureAppenderTest {

    private static Logger logger;

    private static BuiltConfiguration buildConfiguration() {

        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory
                .newConfigurationBuilder();

        AppenderComponentBuilder innerAppender = builder
                .newAppender("console", "Console")
                .add(builder.newLayout("PatternLayout")
                        .addAttribute("pattern", "%sign %msg%n"));

        AppenderComponentBuilder signatureAppender = builder
                .newAppender("signatureAppender", "core.appender.signature.SignatureAppender")
                .addAttribute("Appender", innerAppender);

        RootLoggerComponentBuilder rootLogger = builder
                .newRootLogger(Level.ERROR)
                .add(builder.newAppenderRef("signatureAppender"));

        builder.add(rootLogger);

        return builder.build();
    }

    @BeforeAll
    public static void setup() {

        Configurator.initialize(buildConfiguration());
        logger = LogManager.getLogger(SignatureAppenderTest.class);
    }

    @Test
    public void configurationWorks() {
        //TODO: Assert configuration working
    }
}