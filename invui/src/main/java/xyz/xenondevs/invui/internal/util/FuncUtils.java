package xyz.xenondevs.invui.internal.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import xyz.xenondevs.invui.InvUI;

import java.util.function.Function;
import java.util.function.Supplier;

public final class FuncUtils {
    
    private FuncUtils() {}
    
    /**
     * Resolves the value from the given supplier, catching any exceptions that may occur and
     * returns the fallback value if an exception is thrown.
     *
     * @param supplier the supplier to resolve the value from
     * @param fallback the fallback value to return if an exception is thrown
     * @param <T>      the type of the value returned by the supplier
     * @return the resolved value from the supplier, or the fallback value if an exception is thrown
     */
    @NullUnmarked
    public static <T> T getSafely(@NonNull Supplier<? extends T> supplier, T fallback) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            InvUI.getInstance().handleException("Failed to get value from supplier", t);
            return fallback;
        }
    }
    
    /**
     * Applies the given function to the provided value, catching any exceptions that may occur and
     * returns the fallback value if an exception is thrown.
     *
     * @param function the function to apply to the value
     * @param value    the value to apply the function to
     * @param fallback the fallback value to return if an exception is thrown
     * @param <T>      the type of the input value
     * @param <R>      the type of the result returned by the function
     * @return the result of applying the function to the value, or the fallback value if an exception is thrown
     */
    @NullUnmarked
    public static <T, R> R applySafely(@NonNull Function<? super T, ? extends R> function, T value, R fallback) {
        try {
            return function.apply(value);
        } catch (Throwable t) {
            InvUI.getInstance().handleException("Failed to apply function", t);
            return fallback;
        }
    }
    
}
