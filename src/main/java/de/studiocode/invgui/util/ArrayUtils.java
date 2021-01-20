package de.studiocode.invgui.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
    
    public static int findFirstEmptyIndex(@NotNull Object[] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == null) return index;
        }
        
        return -1;
    }
    
    public static List<Integer> findAllOccurrences(@NotNull Object[] array, @NotNull Object toFind) {
        List<Integer> occurrences = new ArrayList<>();
        
        for (int index = 0; index < array.length; index++) {
            Object obj = array[index];
            if (obj != null && obj.equals(toFind)) occurrences.add(index);
        }
        
        return occurrences;
    }
    
}
