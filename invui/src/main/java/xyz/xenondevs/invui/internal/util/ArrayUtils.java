package xyz.xenondevs.invui.internal.util;

import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ArrayUtils {
    
    public static int findFirstEmptyIndex(@Nullable Object[] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == null) return index;
        }
        
        return -1;
    }
    
    public static <T> Map<Integer, T> findAllOccurrences(@Nullable T[] array, Predicate<T> predicate) {
        Map<Integer, T> occurrences = new HashMap<>();
        
        for (int index = 0; index < array.length; index++) {
            T t = array[index];
            if (predicate.test(t)) occurrences.put(index, t);
        }
        
        return occurrences;
    }
    
    @NullUnmarked
    @SuppressWarnings("unchecked")
    public static <T> T[] concant(T first, T[] rest) {
        T[] result = (T[]) new Object[rest.length + 1];
        result[0] = first;
        System.arraycopy(rest, 0, result, 1, rest.length);
        return result;
    }
    
}
