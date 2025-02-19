package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

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
        var items = new ItemStack[] {new ItemStack(Material.DIRT), new ItemStack(Material.AIR)};
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
    
}