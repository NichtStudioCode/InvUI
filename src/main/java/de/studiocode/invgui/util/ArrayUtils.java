package de.studiocode.invgui.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ArrayUtils {
    
    public static int findFirstEmptyIndex(@NotNull Object[] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == null) return index;
        }
        
        return -1;
    }
    
//    public static <T> List<Integer> findAllOccurrences(@NotNull T[] array, @NotNull T toFind) {
//        List<Integer> occurrences = new ArrayList<>();
//        
//        for (int index = 0; index < array.length; index++) {
//            T t = array[index];
//            if (t != null && t.equals(toFind)) occurrences.add(index);
//        }
//        
//        return occurrences;
//    }
    
    public static <T> Map<Integer, T> findAllOccurrences(@NotNull T[] array, Predicate<T> predicate) {
        Map<Integer, T> occurrences = new HashMap<>();
        
        for (int index = 0; index < array.length; index++) {
            T t = array[index];
            if (predicate.test(t)) occurrences.put(index, t);
        }
        
        return occurrences;
    }
    
}
