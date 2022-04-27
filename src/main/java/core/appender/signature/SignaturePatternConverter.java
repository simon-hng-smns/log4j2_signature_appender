package core.appender.signature;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

import java.util.Objects;

@Plugin(name = "core.appender.signature.SignaturePatternConverter", category = "Converter")
@ConverterKeys({ "sign", "signature" })
public class SignaturePatternConverter extends LogEventPatternConverter {

    protected SignaturePatternConverter(String[] options) {

        super("Signed", "signed");
    }

    public static SignaturePatternConverter newInstance(String[] options) {

        return new SignaturePatternConverter(options);
    }

    @Override public void format(LogEvent event, StringBuilder toAppendTo) {

        String hash = event.getContextData().getValue("signature");
        if (Objects.nonNull(hash)) {
            toAppendTo.append(hash);
        }
    }
}
