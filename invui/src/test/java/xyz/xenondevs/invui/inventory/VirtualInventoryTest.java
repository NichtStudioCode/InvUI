package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

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
    void testThrowOnAirInConstructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var items = new ItemStack[] {new ItemStack(Material.DIRT), new ItemStack(Material.AIR)};
            new VirtualInventory(null, items);
        });
        Assertions.assertDoesNotThrow(() -> {
            var items = new ItemStack[] {new ItemStack(Material.DIRT), null};
            new VirtualInventory(null, items);
        });
    }
    
}