package core.appender.signature;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.util.SecretKeyProvider;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * Outer appender that signs each log event and delegates to an inner appender
 */
@Plugin(name = "SignatureAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public final class SignatureAppender extends AbstractAppender {

    @PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {

        return new Builder<B>().asBuilder();
    }

    public static class Builder<B extends Builder<B>>
            extends AbstractAppender.Builder<B>
            implements org.apache.logging.log4j.core.util.Builder<AbstractAppender> {

        @PluginBuilderAttribute("SecretKeyProvider")
        @Required(message = "You need to implement the SecretKeyProvider interface and supply its binary name here")
        private String secretKeyProvider;

        @PluginBuilderAttribute("HashStrategy")
        @Required(message = "You need to implement the core.appender.signature.HashStrategy interface and supply its binary name here")
        private String hashStrategy;

        @PluginElement("Appender")
        @Required
        private AbstractAppender innerAppender;

        @Override
        public AbstractAppender build() {

            return new SignatureAppender(getName(), getFilter(), innerAppender, secretKeyProvider,
                    hashStrategy
            );
        }

    }

    private final SecretKeyProvider secretKeyProvider;

    private final HashStrategy hashStrategy;

    private final AbstractAppender innerAppender;

    private final PseudoByteBufferDestination bufferDestination;

    private final LogEventFactory logEventFactory;

    private final Layout<? extends Serializable> layout;

    private String lastHash;

    /**
     * @param name                       The name of the appender
     * @param filter                     The filter, if any, to use.
     * @param secretKeyProviderClassName {@link ClassLoader binary name} of the {@link SecretKeyProvider} implementation.
     * @param hashStrategyClassName      {@link ClassLoader binary name} of the {@link HashStrategy} implementation
     * @param innerAppender              An inner appender, that must supply a pattern layout
     *                                   which contains the {@link SignaturePatternConverter}
     *                                   in order to append the signature.
     * @return the core.appender.signature.SignatureAppender
     */

    protected SignatureAppender(String name,
            Filter filter,
            AbstractAppender innerAppender,
            String secretKeyProviderClassName,
            String hashStrategyClassName
    ) {

        /*
         * The signappender does not actually have it's own layout but uses the layout
         * of its inner appender
         */

        super(name, filter, null);

        this.bufferDestination = new PseudoByteBufferDestination();
        this.logEventFactory = new DefaultLogEventFactory();
        this.innerAppender = innerAppender;
        this.layout = Objects.requireNonNull(innerAppender.getLayout(),
                "inner appender must directly supply a layout");

        this.secretKeyProvider = (SecretKeyProvider) getClassFromName(secretKeyProviderClassName);
        this.hashStrategy = (HashStrategy) getClassFromName(hashStrategyClassName);
    }

    private Object getClassFromName(String className) {

        try {
            Class<?> clazz = getClass().getClassLoader()
                    .loadClass(className);

            return clazz.getConstructor().newInstance();

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            return new IllegalArgumentException(className + " does is not a valid argument");
        }
    }

    @Override public void append(LogEvent event) {

        layout.encode(event, bufferDestination);

        byte[] formattedEvent = bufferDestination.getData();

        if (Objects.isNull(lastHash))
            lastHash = getHashFromFile();

        String hash = new String(
                hashStrategy.hash(formattedEvent, secretKeyProvider.getSecretKey()));
        lastHash = hash;

        writeHashToFile(lastHash);

        List<Property> hashProperty = List.of(Property.createProperty("hash", hash));

        LogEvent hashedEvent = logEventFactory.createEvent(
                event.getLoggerName(), event.getMarker(), event.getLoggerFqcn(),
                event.getLevel(), event.getMessage(), hashProperty, null);

        innerAppender.append(hashedEvent);
    }

    private void writeHashToFile(String hash) {
        //TODO:
    }

    private String getHashFromFile() {
        //TODO:
        return "";
    }

}