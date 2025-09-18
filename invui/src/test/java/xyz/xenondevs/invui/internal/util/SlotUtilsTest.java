package xyz.xenondevs.invui.internal.util;

import org.junit.jupiter.api.Test;
import xyz.xenondevs.invui.gui.Slot;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlotUtilsTest {
    
    @Test
    public void testDetermineHorizontalLinesLengthEven5() {
        var slots = List.of(
            s(1, 0), s(2, 0), s(3, 0), s(4, 0), s(5, 0),
            s(1, 1), s(2, 1), s(3, 1), s(4, 1), s(5, 1),
            s(1, 2), s(2, 2), s(3, 2), s(4, 2), s(5, 2)
        );
        
        assertEquals(5, SlotUtils.determineLongestHorizontalLineLength(slots, 3));
    }
    
    @Test
    public void testDetermineHorizontalLinesLengthEven5Reversed() {
        var slots = List.of(
            s(1, 0), s(2, 0), s(3, 0), s(4, 0), s(5, 0),
            s(1, 1), s(2, 1), s(3, 1), s(4, 1), s(5, 1),
            s(1, 2), s(2, 2), s(3, 2), s(4, 2), s(5, 2)
        ).reversed();
        
        assertEquals(5, SlotUtils.determineLongestHorizontalLineLength(slots, 3));
    }
    
    @Test
    public void testDetermineHorizontalLinesLength5Single() {
        var slots = List.of(
            s(0, 0), s(1, 0), s(2, 0), s(3, 0), s(4, 0)
        );
        
        assertEquals(5, SlotUtils.determineLongestHorizontalLineLength(slots, 1));
    }
    
    @Test
    public void testDetermineHorizontalLinesLengthUneven() {
        var slots = List.of(
            s(0, 0), s(1, 0), s(2, 0),
            s(1, 1), s(2, 1), s(3, 1), s(4, 1), s(5, 1),
            s(0, 2)
        );
        
        assertEquals(5, SlotUtils.determineLongestHorizontalLineLength(slots, 3));
    }
    
    @Test
    public void testDetermineHorizontalLinesLengthNonContinuous() {
        var slots = List.of(
            s(0, 0), /* 1,0 2,0 3,0 4,0 */ s(5, 0),
            s(0, 0), s(1, 0)
        );
        assertEquals(6, SlotUtils.determineLongestHorizontalLineLength(slots, 2));
    }
    
    @Test
    public void testDetermineVerticalLinesLength5Even() {
        var slots = List.of(
            s(1, 1), s(2, 1), s(3, 1),
            s(1, 2), s(2, 2), s(3, 2),
            s(1, 3), s(2, 3), s(3, 3),
            s(1, 4), s(2, 4), s(3, 4),
            s(1, 5), s(2, 5), s(3, 5)
        );
        
        assertEquals(5, SlotUtils.determineLongestVerticalLineLength(slots, 4));
    }
    
    @Test
    public void testDetermineVerticalLinesLength5Single() {
        var slots = List.of(s(0, 0), s(0, 1), s(0, 2), s(0, 3), s(0, 4));
        assertEquals(5, SlotUtils.determineLongestVerticalLineLength(slots, 1));
    }
    
    @Test
    public void testDetermineVerticalLinesLengthUneven() {
        var slots = List.of(
            s(1, 1), s(2, 1), /*    */
            s(1, 2), s(2, 2), /*    */
            s(1, 3), s(2, 3), s(3, 3),
            s(1, 4), /*    */ s(3, 4),
            s(1, 5), /*    */ s(3, 5)
        );
        
        assertEquals(5, SlotUtils.determineLongestVerticalLineLength(slots, 4));
    }
    
    @Test
    public void testDetermineVerticalLinesLengthNonContinuous() {
        var slots = List.of(
            s(1, 0), /*    */ /*    */
            /*    */ s(2, 1), /*    */
            /*    */ s(2, 2), /*    */
            /*    */ s(2, 3), s(3, 3),
            /*    */ /*    */ s(3, 4),
            s(1, 5), /*    */ s(3, 5)
        );
        assertEquals(6, SlotUtils.determineLongestVerticalLineLength(slots, 4));
    }
    
    private Slot s(int x, int y) {
        return new Slot(x, y);
    }
    
}
