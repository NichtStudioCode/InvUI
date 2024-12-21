package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.plugin.PluginMock;
import xyz.xenondevs.invui.InvUI;

class VirtualInventoryTest {
    
    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
        PluginMock plugin = MockBukkit.createMockPlugin();
        InvUI.getInstance().setPlugin(plugin);
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