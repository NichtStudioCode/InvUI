package xyz.xenondevs.invui.internal.util;

import xyz.xenondevs.invui.InvUI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
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
    
    /**
     * Creates a new {@link List} of the specified size, filled with the results of the
     * specified {@link Function initializer}.
     *
     * @param size        The size of the list to create
     * @param initializer The {@link Function} to use to initialize the elements of the list
     * @param <T>         The type of the elements in the list
     * @return A new {@link List} of the specified size, filled with the results of the
     */
    public static <T> List<T> create(int size, Function<? super Integer, ? extends T> initializer) {
        var list = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            list.add(initializer.apply(i));
        }
        return list;
    }
    
}
