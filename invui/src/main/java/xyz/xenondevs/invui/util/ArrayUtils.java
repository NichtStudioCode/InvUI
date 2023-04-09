package xyz.xenondevs.invui.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class ArrayUtils {
    
    public static int findFirstEmptyIndex(@Nullable Object @NotNull [] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == null) return index;
        }
        
        return -1;
    }
    
    public static @NotNull List<Integer> findEmptyIndices(@Nullable Object @NotNull [] array) {
        List<Integer> emptyIndices = new ArrayList<>();
        for (int index = 0; index < array.length; index++) {
            if (array[index] == null) emptyIndices.add(index);
        }
        
        return emptyIndices;
    }
    
    public static <T> @NotNull Map<Integer, T> findAllOccurrences(@Nullable T @NotNull [] array, Predicate<T> predicate) {
        Map<Integer, T> occurrences = new HashMap<>();
        
        for (int index = 0; index < array.length; index++) {
            T t = array[index];
            if (predicate.test(t)) occurrences.put(index, t);
        }
        
        return occurrences;
    }
    
}
