package xyz.xenondevs.invui.inventory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ObscuredInventoryTest {
    
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
        var inv = new VirtualInventory(5);
        inv.setIterationOrder(IterationOrderCategory.ADD, new int[] {4, 3, 0, 1, 2});
        
        var obscured = new ObscuredInventory(inv, i -> i % 2 == 0);
        assertArrayEquals(new int[] {1, 0}, obscured.getIterationOrder(IterationOrderCategory.ADD));
        assertArrayEquals(new int[] {0, 1}, obscured.getIterationOrder(IterationOrderCategory.COLLECT));
    }
    
}
