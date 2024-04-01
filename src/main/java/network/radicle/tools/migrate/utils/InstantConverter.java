package network.radicle.tools.migrate.utils;

import picocli.CommandLine;
import picocli.CommandLine.TypeConversionException;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class InstantConverter implements CommandLine.ITypeConverter<Instant> {
    @Override
    public Instant convert(String value) {
        try {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value, Instant::from);
        } catch (Exception ex) {
            throw new TypeConversionException(
                    "This timestamp must be in the following format: 2023-01-01T10:15:30+01:00");
        }
    }
}
