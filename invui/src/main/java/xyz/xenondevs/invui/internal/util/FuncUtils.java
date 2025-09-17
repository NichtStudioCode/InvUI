package xyz.xenondevs.invui.internal.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import xyz.xenondevs.invui.InvUI;

import java.util.function.Supplier;
import java.util.logging.Level;

public class FuncUtils {
    
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
            InvUI.getInstance().handleUncaughtException("Failed to get value from supplier", t);
            return fallback;
        }
    }
    
}
