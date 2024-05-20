package xyz.xenondevs.invui.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ArrayUtils {
    
    public static int findFirstEmptyIndex(@Nullable Object @NotNull [] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == null) return index;
        }
        
        return -1;
    }
    
    public static <T> @NotNull Map<Integer, T> findAllOccurrences(@Nullable T @NotNull [] array, Predicate<T> predicate) {
        Map<Integer, T> occurrences = new HashMap<>();
        
        for (int index = 0; index < array.length; index++) {
            T t = array[index];
            if (predicate.test(t)) occurrences.put(index, t);
        }
        
        return occurrences;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T[] concat(@Nullable T first, @Nullable T @NotNull[] rest) {
        T[] result = (T[]) new Object[rest.length + 1];
        result[0] = first;
        System.arraycopy(rest, 0, result, 1, rest.length);
        return result;
    }
    
}
