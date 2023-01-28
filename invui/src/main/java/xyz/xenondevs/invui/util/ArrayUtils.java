package xyz.xenondevs.invui.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ArrayUtils {
    
    public static int findFirstEmptyIndex(@NotNull Object[] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == null) return index;
        }
        
        return -1;
    }
    
    @NotNull
    public static List<Integer> findEmptyIndices(@NotNull Object[] array) {
        List<Integer> emptyIndices = new ArrayList<>();
        for (int index = 0; index < array.length; index++) {
            if (array[index] == null) emptyIndices.add(index);
        }
        
        return emptyIndices;
    }
    
    @NotNull
    public static <T> Map<Integer, T> findAllOccurrences(@NotNull T[] array, Predicate<T> predicate) {
        Map<Integer, T> occurrences = new HashMap<>();
        
        for (int index = 0; index < array.length; index++) {
            T t = array[index];
            if (predicate.test(t)) occurrences.put(index, t);
        }
        
        return occurrences;
    }
    
}
