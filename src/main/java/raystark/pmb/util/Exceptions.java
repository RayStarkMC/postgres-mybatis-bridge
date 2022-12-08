package raystark.pmb.util;

import java.util.function.Supplier;

public final class Exceptions {
    private Exceptions() {
        throw new AssertionError();
    }

    public static <R, X extends Exception> R throwing(Supplier<? extends X> exceptionSupplier) throws X{
        throw exceptionSupplier.get();
    }
}
