package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import xyz.xenondevs.invui.item.ItemWrapper;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class VirtualInventoryTest {
    
    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
    }
    
    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    void testFilterAirInConstructor() {
        var items = new ItemStack[] {ItemStack.of(Material.DIRT), ItemStack.of(Material.AIR)};
        var inv = new VirtualInventory(null, items);
        
        assertNull(inv.getItem(1));
    }
    
    @Test
    void testCollectSimilar() {
        var items = new @Nullable ItemStack[] {
            ItemStack.of(Material.DIRT, 4),
            ItemStack.of(Material.DIAMOND, 64),
            ItemStack.of(Material.DIAMOND, 5),
            null,
            ItemStack.of(Material.DIAMOND, 3),
            null,
            null,
        };
        
        var inv = new VirtualInventory(null, items);
        inv.collectSimilar(null, ItemStack.of(Material.DIAMOND), 1);
        
        assertArrayEquals(
            new @Nullable ItemStack[] {
                ItemStack.of(Material.DIRT, 4),
                ItemStack.of(Material.DIAMOND, 9),
                null,
                null,
                null,
                null,
                null,
            },
            inv.getItems()
        );
    }

    @Test
    void testVisualizerCachingAndInvalidation() {
        var inv = new VirtualInventory(3);
        inv.setItem(null, 0, ItemStack.of(Material.DIAMOND));

        var calls = new AtomicInteger();
        inv.setVisualizer(item -> {
            calls.incrementAndGet();
            return item == null ? null : new ItemWrapper(ItemStack.of(Material.GOLD_INGOT));
        });

        // cache hit returns the same instance
        var p = inv.getVisualization(0);
        assertSame(p, inv.getVisualization(0));
        assertEquals(1, calls.get());

        // empty slots are also cached
        assertSame(null, inv.getVisualization(1));
        assertSame(null, inv.getVisualization(1));
        assertEquals(2, calls.get());

        // setItem invalidates cache
        inv.setItem(null, 0, ItemStack.of(Material.IRON_INGOT));
        inv.getVisualization(0);
        assertEquals(3, calls.get());

        // notifyWindows(slot) invalidates that slot
        inv.notifyWindows(0);
        inv.getVisualization(0);
        assertEquals(4, calls.get());

        // notifyWindows() invalidates all slots
        inv.notifyWindows();
        inv.getVisualization(0);
        inv.getVisualization(1);
        assertEquals(6, calls.get());
        
        // switching the visualizer invalidates all slots
        inv.setVisualizer(_ -> {
            calls.incrementAndGet();
            return null;
        });
        inv.getVisualization(0);
        inv.getVisualization(1);
        assertEquals(8, calls.get());
    }

    @Test
    void testVisualizerReceivesItemCopy() {
        var inv = new VirtualInventory(3);
        inv.setItem(null, 0, ItemStack.of(Material.DIAMOND, 5));
        inv.setVisualizer(item -> {
            if (item != null) item.setAmount(99); // mutate to verify it's a copy
            return null;
        });

        inv.getVisualization(0);
        assertEquals(5, inv.getItem(0).getAmount());
    }

    @Test
    void testNoVisualizersReturnsNull() {
        var inv = new VirtualInventory(3);
        inv.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        assertNull(inv.getVisualization(0));
        assertNull(inv.getVisualization(1));
    }

}