package xyz.xenondevs.invui.internal.util;

import org.jspecify.annotations.NullUnmarked;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntFunction;

public class ArrayUtils {
    
    /**
     * Concatenates a single element with an array.
     *
     * @param newArray The function to create a new array
     * @param first    The first element
     * @param rest     The rest of the elements
     * @param <T>      The type of the array
     * @return The concatenated array
     */
    @NullUnmarked
    public static <T> T[] concat(IntFunction<T[]> newArray, T first, T[] rest) {
        T[] result = newArray.apply(rest.length + 1);
        result[0] = first;
        System.arraycopy(rest, 0, result, 1, rest.length);
        return result;
    }
    
    /**
     * Concatenates a single char with a char array.
     *
     * @param first The first char
     * @param rest  The rest of the chars
     * @return The concatenated char array
     */
    public static char[] concat(char first, char[] rest) {
        char[] result = new char[rest.length + 1];
        result[0] = first;
        System.arraycopy(rest, 0, result, 1, rest.length);
        return result;
    }
    
    /**
     * Creates a copy of the original array, truncating or padding with
     * the given value if necessary.
     *
     * @param original The original array
     * @param size     The size of the new array
     * @param padding  The value to pad with
     * @return The new array
     */
    public static int[] copyOf(int[] original, int size, int padding) {
        int[] copy = Arrays.copyOf(original, size);
        if (size > original.length) {
            Arrays.fill(copy, original.length, size, padding);
        }
        return copy;
    }
    
    /**
     * Creates a set of the given characters.
     *
     * @param first The first character
     * @param rest  The rest of the characters
     * @return The set of characters
     */
    public static Set<Character> toSet(char first, char[] rest) {
        if (rest.length == 0)
            return Set.of(first);
        
        var set = new HashSet<Character>(first + rest.length);
        set.add(first);
        for (char c : rest) {
            set.add(c);
        }
        return set;
    }
    
}
