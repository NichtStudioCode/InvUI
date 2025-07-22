package xyz.xenondevs.invui.inventory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CompositeInventoryTest {
    
    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
    }
    
    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    public void testIterationOrder() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        inv2.reverseIterationOrder();
        var inv3 = new VirtualInventory(3);
        inv3.setIterationOrder(OperationCategory.ADD, new int[] {2, 0, 1});
        
        var composite = new CompositeInventory(inv1, inv2, inv3);
        assertArrayEquals(new int[] {0, 1, 2, 5, 4, 3, 8, 6, 7}, composite.getIterationOrder(OperationCategory.ADD));
        assertArrayEquals(new int[] {0, 1, 2, 5, 4, 3, 6, 7, 8}, composite.getIterationOrder(OperationCategory.COLLECT));
    }
    
}
