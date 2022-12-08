package raystark.pmb.range;

sealed public interface TSRange {
    record Empty() implements TSRange {}

    record NonEmpty(Bound lowerBound, Bound upperBound) implements TSRange {}
}
