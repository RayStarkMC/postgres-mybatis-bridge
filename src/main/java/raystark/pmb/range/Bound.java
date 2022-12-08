package raystark.pmb.range;

import java.time.Instant;

sealed public interface Bound {
    record Inclusive(Instant bound) implements Bound {}

    record Exclusive(Instant bound) implements Bound {}

    record Infinity() implements Bound {}
}
