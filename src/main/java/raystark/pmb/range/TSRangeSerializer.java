package raystark.pmb.range;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public final class TSRangeSerializer {

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_DATE)
        .appendLiteral(" ")
        .append(DateTimeFormatter.ISO_TIME)
        .toFormatter();

    public String serialize(TSRange tsRange) {
        return switch (tsRange) {
            case TSRange.Empty() -> "empty";
            case TSRange.NonEmpty(var lowerBound, var upperBound) -> {
                var lowerString = switch (lowerBound) {
                    case Bound.Inclusive(var instant) -> "[" + format(instant);
                    case Bound.Exclusive(var instant)  -> "(" + format(instant);
                    case Bound.Infinity() -> "(";
                };

                var upperString = switch (upperBound) {
                    case Bound.Inclusive(var instant) -> format(instant) + "]";
                    case Bound.Exclusive(var instant) -> format(instant) + ")";
                    case Bound.Infinity() -> ")";
                };

                yield lowerString + "," + upperString;
            }
        };
    }

    private String format(Instant instant) {
        return FORMATTER.format(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
    }
}
