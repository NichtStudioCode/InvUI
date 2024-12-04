package xyz.xenondevs.invui.util;

/**
 * A functional interface that takes three arguments and returns no result.
 *
 * @param <A> The first argument type
 * @param <B> The second argument type
 * @param <C> The third argument type
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {
    
    /**
     * Performs this operation on the given arguments.
     *
     * @param a The first operand
     * @param b The second operand
     * @param c The third operand
     */
    void accept(A a, B b, C c);
    
    /**
     * Returns a composed {@code TriConsumer} that performs, in sequence, this operation followed by the {@code after} operation.
     *
     * @param after The operation to perform after this operation
     * @return A composed {@code TriConsumer} that performs in sequence this operation followed by the {@code after} operation
     */
    default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> after) {
        return (a, b, c) -> {
            accept(a, b, c);
            after.accept(a, b, c);
        };
    }
    
}
