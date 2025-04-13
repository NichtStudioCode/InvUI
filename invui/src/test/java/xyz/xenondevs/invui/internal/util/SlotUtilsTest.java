package xyz.xenondevs.invui.internal.util;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.SequencedSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SlotUtilsTest {
    
    @Test
    public void testDetermineHorizontalLinesLengthEven5() {
        var slots = sequencedIntSetOf(
            0, 1, 2, 3, 4, // 5, 6, 7, 8, length 5
            9, 10, 11, 12, 13, // 14, 15, 16, 17, length 5
            18, 19, 20, 21, 22 // 23, 24, 25, 26, length 5
        );
        
        assertEquals(5, SlotUtils.determineHorizontalLinesLength(slots, 9));
    }
    
    @Test
    public void testDetermineHorizontalLinesLengthEven5Reversed() {
        var slots = sequencedIntSetOf(
            0, 1, 2, 3, 4, // 5, 6, 7, 8, length 5
            9, 10, 11, 12, 13, // 14, 15, 16, 17, length 5
            18, 19, 20, 21, 22 // 23, 24, 25, 26, length 5
        ).reversed();
        
        assertEquals(5, SlotUtils.determineHorizontalLinesLength(slots, 9));
    }
    
    @Test
    public void testDetermineHorizontalLinesLength5Single() {
        var slots = sequencedIntSetOf(
            0, 1, 2, 3, 4 // 5, 6, 7, 8, length 5
        );
        
        assertEquals(5, SlotUtils.determineHorizontalLinesLength(slots, 9));
    }
    
    @Test
    public void testDetermineHorizontalLineLengthCompleteLine() {
        var slots = sequencedIntSetOf(0, 1, 2, 3, 4, 5, 6, 7, 8);
        
        assertEquals(9, SlotUtils.determineHorizontalLinesLength(slots, 9));
    }
    
    @Test
    public void testDetermineHorizontalLinesLengthUneven() {
        var slots = sequencedIntSetOf(
            0, 1, 2, // 3, 4, 5, 6, 7, 8, length 3
            9, 10, 11, 12, 13, // 14, 15, 16, 17, length 5
            18 // length 1
        );
        
        assertThrows(
            IllegalArgumentException.class,
            () -> SlotUtils.determineHorizontalLinesLength(slots, 9)
        );
    }
    
    @Test
    public void testDetermineVerticalLinesLength5Even() {
        /*
         * 01 . 06 11 . . . . .
         * 02 . 07 12 . . . . .
         * 03 . 08 13 . . . . .
         * 04 . 09 14 . . . . .
         * 05 . 10 15 . . . . .
         */ 
        var slots = sequencedIntSetOf(
            0, 9, 18, 27, 36,
            2, 11, 20, 29, 38,
            3, 12, 21, 30, 39
        );
        
        assertEquals(5, SlotUtils.determineVerticalLinesLength(slots, 9));
    }
    
    @Test
    public void testDetermineVerticalLinesLengthUneven() {
        /*
         * 01 . 04 07 . . . . .
         * 02 . .  08 . . . . .
         * 03 . .  09 . . . . .
         * .  . 05 10 . . . . .
         * .  . 06 11 . . . . .
         */
        var slots = sequencedIntSetOf(
            0, 9, 18,
            2, 29, 38,
            3, 12, 21, 30, 39
        );
        
        assertThrows(
            IllegalArgumentException.class,
            () -> SlotUtils.determineVerticalLinesLength(slots, 9)
        );
    }
    
    @Test
    public void testDetermineVerticalLinesLength5Single() {
        var slots = sequencedIntSetOf(0, 9, 18, 27, 36);
        assertEquals(5, SlotUtils.determineVerticalLinesLength(slots, 9));
    }
    
    @Test
    public void testDetermineVerticalLineLengthCompleteLine() {
        var slots = sequencedIntSetOf(0, 1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(1, SlotUtils.determineVerticalLinesLength(slots, 9));
    }
    
    private SequencedSet<Integer> sequencedIntSetOf(int... ints) {
        var set = new LinkedHashSet<Integer>();
        for (int i : ints) {
            set.add(i);
        }
        return set;
    }
    
}
