import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.util.SecretKeyProvider;

import java.io.Serializable;
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

        @PluginElement("SecretKeyProvider")
        @Required(message = "You need to implement the SecretKeyProvider interface and supply it here")
        private SecretKeyProvider secretKeyProvider;

        @PluginElement("HashStrategy")
        @Required(message = "You need to implement the HashStrategy interface and supply it here")
        private HashStrategy hashStrategy;

        @PluginElement("Appender")
        @Required
        private AbstractAppender innerAppender;

        @Override
        public AbstractAppender build() {

            return new SignatureAppender(getName(), getFilter(), secretKeyProvider, hashStrategy,
                    innerAppender);
        }

    }

    private final SecretKeyProvider secretKeyProvider;

    private final HashStrategy hashStrategy;

    private final AbstractAppender innerAppender;

    private final PseudoByteBufferDestination bufferDestination;

    private final LogEventFactory logEventFactory;

    private final Layout<? extends Serializable> layout;

    /**
     * @param name              The name of the appender
     * @param filter            The filter, if any, to use.
     * @param secretKeyProvider {@link SecretKeyProvider} implementation.
     * @param hashStrategy      {@link HashStrategy} implementation
     * @param innerAppender     An inner appender, that must supply a pattern layout
     *                          which contains the {@link SignaturePatternConverter}
     *                          in order to append the signature.
     * @return the SignatureAppender
     */

    protected SignatureAppender(String name,
            Filter filter,
            SecretKeyProvider secretKeyProvider, HashStrategy hashStrategy,
            AbstractAppender innerAppender) {

        /*
         * The signappender does not actually have it's own layout but uses the layout
         * of its inner appender
         */

        super(name, filter, null);

        this.secretKeyProvider = secretKeyProvider;
        this.hashStrategy = hashStrategy;
        this.bufferDestination = new PseudoByteBufferDestination();
        this.logEventFactory = new DefaultLogEventFactory();
        this.innerAppender = innerAppender;
        this.layout = Objects.requireNonNull(innerAppender.getLayout(),
                "inner appender must directly supply a layout");
    }

    @Override public void append(LogEvent event) {

        layout.encode(event, bufferDestination);

        byte[] formattedEvent = bufferDestination.getData();

        String hash = new String(
                hashStrategy.hash(formattedEvent, secretKeyProvider.getSecretKey()));
        List<Property> hashProperty = List.of(Property.createProperty("hash", hash));

        LogEvent hashedEvent = logEventFactory.createEvent(
                event.getLoggerName(), event.getMarker(), event.getLoggerFqcn(),
                event.getLevel(), event.getMessage(), hashProperty, null);

        innerAppender.append(hashedEvent);
    }

}