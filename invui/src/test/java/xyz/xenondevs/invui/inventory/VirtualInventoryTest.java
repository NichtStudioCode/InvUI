package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
    
}