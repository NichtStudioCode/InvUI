package xyz.xenondevs.invui.internal.util;

import xyz.xenondevs.invui.InvUI;

import java.util.function.Consumer;
import java.util.logging.Level;

public class CollectionUtils {
    
    /**
     * Runs the specified {@link Consumer action} for each element in the {@link Iterable}, catching all
     * {@link Throwable Throwables} and logging them with the specified message.
     *
     * @param iterable The {@link Iterable} to iterate over
     * @param action   The {@link Consumer action} to run for each element
     * @param message  The message to log in case of an exception
     * @param <T>      The type of the elements in the {@link Iterable}
     */
    public static <T> void forEachCatching(Iterable<? extends T> iterable, Consumer<? super T> action, String message) {
        int i = 0;
        for (var obj : iterable) {
            try {
                action.accept(obj);
                i++;
            } catch (Throwable t) {
                InvUI.getInstance().getLogger().log(Level.SEVERE, message + " (" + i + ")", t);
            }
        }
    }
    
}
