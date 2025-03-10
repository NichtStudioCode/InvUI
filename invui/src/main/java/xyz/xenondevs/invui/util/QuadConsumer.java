package xyz.xenondevs.invui.util;

/**
 * A functional interface that takes four arguments and returns no result.
 *
 * @param <A> The first argument type
 * @param <B> The second argument type
 * @param <C> The third argument type
 * @param <D> The fourth argument type
 */
@FunctionalInterface
public interface QuadConsumer<A, B, C, D> {
    
    /**
     * Performs this operation on the given arguments.
     *
     * @param a The first operand
     * @param b The second operand
     * @param c The third operand
     * @param d The fourth operand
     */
    void accept(A a, B b, C c, D d);
    
    /**
     * Returns a composed {@code QuadConsumer} that performs, in sequence, this operation followed by the {@code after} operation.
     *
     * @param after The operation to perform after this operation
     * @return A composed {@code QuadConsumer} that performs in sequence this operation followed by the {@code after} operation
     */
    default QuadConsumer<A, B, C, D> andThen(QuadConsumer<A, B, C, D> after) {
        return (a, b, c, d) -> {
            accept(a, b, c, d);
            after.accept(a, b, c, d);
        };
    }
    
}
