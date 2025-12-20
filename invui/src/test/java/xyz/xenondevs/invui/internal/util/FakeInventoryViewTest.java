package xyz.xenondevs.invui.internal.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import static org.junit.jupiter.api.Assertions.*;

class FakeInventoryViewTest {
    
    private static ServerMock server;
    private static final NamespacedKey ITEM_KEY = new NamespacedKey("invui", "item");
    
    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        InvUI.getInstance().setExceptionHandler((msg, t) -> {
            throw new AssertionError(msg, t);
        });
    }
    
    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    void getTopInventory() {
        var inv = Bukkit.createInventory(null, 9);
        var player = server.addPlayer();
        var view = new FakeInventoryView(player, inv);
        assertEquals(inv, view.getTopInventory());
    }
    
    @Test
    void getBottomInventory() {
        var player = server.addPlayer();
        var view = new FakeInventoryView(player, Bukkit.createInventory(null, 9));
        assertEquals(player.getInventory(), view.getBottomInventory());
    }
    
    @Test
    void getPlayer() {
        var player = server.addPlayer();
        var view = new FakeInventoryView(player, Bukkit.createInventory(null, 9));
        assertEquals(player, view.getPlayer());
    }
    
    @Test
    void getType() {
        var player = server.addPlayer();
        var view = new FakeInventoryView(player, Bukkit.createInventory(null, 9));
        // FakeInventoryView always uses CHEST
        assertEquals(InventoryType.CHEST, view.getType());
    }
    
    @Test
    void setGetItem() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9 * 3);
        var view = new FakeInventoryView(player, top);
        
        for (int i = 0; i < (9 * 3 + 9 * 4); i++) {
            var item = ItemStack.of(Material.DIAMOND);
            var meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.INTEGER, i);
            item.setItemMeta(meta);
            view.setItem(i, item);
        }
        
        for (int i = 0; i < (9 * 3); i++) {
            var item = top.getItem(i);
            assertNotNull(item, "at " + i);
            assertEquals(i, item.getPersistentDataContainer().get(ITEM_KEY, PersistentDataType.INTEGER));
        }
        
        var bot = player.getInventory();
        
        // non hotbar contents, starting at 9
        for (int i = 0; i < (9 * 3); i++) {
            var item = bot.getItem(i + 9);
            assertNotNull(item, "at " + (i + 9));
            assertEquals(i + (9 * 3), item.getPersistentDataContainer().get(ITEM_KEY, PersistentDataType.INTEGER));
        }
        
        // hotbar contents
        for (int i = 0; i < 9; i++) {
            var item = bot.getItem(i);
            assertNotNull(item, "at " + i);
            assertEquals(i + (9 * 3) + (9 * 3), item.getPersistentDataContainer().get(ITEM_KEY, PersistentDataType.INTEGER));
        }
    }
    
    @Test
    void setCursor() {
        var player = server.addPlayer();
        var view = new FakeInventoryView(player, Bukkit.createInventory(null, 9));
        var item = ItemStack.of(Material.DIAMOND);
        view.setCursor(item);
        assertEquals(item, player.getItemOnCursor());
    }
    
    @Test
    void getCursor() {
        var player = server.addPlayer();
        var view = new FakeInventoryView(player, Bukkit.createInventory(null, 9));
        var item = ItemStack.of(Material.DIAMOND);
        player.setItemOnCursor(item);
        assertEquals(item, view.getCursor());
    }
    
    @Test
    void getInventory() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9 * 3);
        var view = new FakeInventoryView(player, top);
        
        for (int i = 0; i < (9 * 3 + 9 * 4); i++) {
            var inv = view.getInventory(i);
            if (i < (9 * 3)) {
                assertEquals(top, inv);
            } else {
                assertEquals(player.getInventory(), inv);
            }
        }
        
        assertNull(view.getInventory(9 * 3 + 9 * 4));
        assertNull(view.getInventory(InventoryView.OUTSIDE));
    }
    
    @Test
    void convertSlot() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9 * 3);
        var view = new FakeInventoryView(player, top);
        
        for (int i = 0; i < (9 * 3); i++) {
            assertEquals(i, view.convertSlot(i));
        }
        
        // non hotbar contents, starting at 9
        for (int i = 0; i < (9 * 3); i++) {
            assertEquals(i + 9, view.convertSlot(i + (9 * 3)));
        }
        
        // hotbar contents
        for (int i = 0; i < 9; i++) {
            assertEquals(i, view.convertSlot(i + (9 * 3) + (9 * 3)));
        }
    }
    
    @Test
    void getSlotType() {
        
    }
    
    @Test
    void countSlots() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9 * 3);
        var view = new FakeInventoryView(player, top);
        assertEquals((9 * 3) + player.getInventory().getSize(), view.countSlots());
    }
    
    @SuppressWarnings("UnstableApiUsage")
    @Test
    void getMenuType() {
        var player = server.addPlayer();
        var top9x1 = Bukkit.createInventory(null, 9);
        var top9x2 = Bukkit.createInventory(null, 9 * 2);
        var top9x3 = Bukkit.createInventory(null, 9 * 3);
        var top9x4 = Bukkit.createInventory(null, 9 * 4);
        var top9x5 = Bukkit.createInventory(null, 9 * 5);
        var top9x6 = Bukkit.createInventory(null, 9 * 6);
        var top5x5 = new VirtualInventory(5 * 5).asBukkitInventory();
        var view9x1 = new FakeInventoryView(player, top9x1);
        var view9x2 = new FakeInventoryView(player, top9x2);
        var view9x3 = new FakeInventoryView(player, top9x3);
        var view9x4 = new FakeInventoryView(player, top9x4);
        var view9x5 = new FakeInventoryView(player, top9x5);
        var view9x6 = new FakeInventoryView(player, top9x6);
        var view5x5 = new FakeInventoryView(player, top5x5);
        
        assertEquals(MenuType.GENERIC_9X1, view9x1.getMenuType());
        assertEquals(MenuType.GENERIC_9X2, view9x2.getMenuType());
        assertEquals(MenuType.GENERIC_9X3, view9x3.getMenuType());
        assertEquals(MenuType.GENERIC_9X4, view9x4.getMenuType());
        assertEquals(MenuType.GENERIC_9X5, view9x5.getMenuType());
        assertEquals(MenuType.GENERIC_9X6, view9x6.getMenuType());
        assertNull(view5x5.getMenuType());
    }
}