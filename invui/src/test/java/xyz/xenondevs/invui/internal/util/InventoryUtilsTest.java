package xyz.xenondevs.invui.internal.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.InvUI;

import static org.junit.jupiter.api.Assertions.*;

class InventoryUtilsTest {
    
    private static ServerMock server;
    
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
    void simulateItemDrag_emptyCursor_returnsEmptyMap() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.empty();
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.LEFT, view, slots, cursor);
        
        assertTrue(result.isEmpty());
    }
    
    @Test
    void simulateItemDrag_leftDrag_distributesEvenly() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 9);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.LEFT, view, slots, cursor);
        
        assertEquals(3, result.size());
        assertEquals(3, result.get(0).getAmount());
        assertEquals(3, result.get(1).getAmount());
        assertEquals(3, result.get(2).getAmount());
        assertEquals(0, cursor.getAmount());
    }
    
    @Test
    void simulateItemDrag_rightDrag_distributesOnePerSlot() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 9);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.RIGHT, view, slots, cursor);
        
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getAmount());
        assertEquals(1, result.get(1).getAmount());
        assertEquals(1, result.get(2).getAmount());
        assertEquals(6, cursor.getAmount());
    }
    
    @Test
    void simulateItemDrag_leftDrag_addsToExistingSimilarItems() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        top.setItem(0, ItemStack.of(Material.DIAMOND, 5));
        top.setItem(1, ItemStack.of(Material.DIAMOND, 3));
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 6);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.LEFT, view, slots, cursor);
        
        assertEquals(3, result.size());
        // Each slot gets 2 items (6 / 3 = 2)
        assertEquals(7, result.get(0).getAmount()); // 5 + 2
        assertEquals(5, result.get(1).getAmount()); // 3 + 2
        assertEquals(2, result.get(2).getAmount()); // 0 + 2
        assertEquals(0, cursor.getAmount());
    }
    
    @Test
    void simulateItemDrag_leftDrag_skipsDifferentItemTypes() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        top.setItem(0, ItemStack.of(Material.DIAMOND, 5));
        top.setItem(1, ItemStack.of(Material.EMERALD, 3)); // Different type
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 6);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.LEFT, view, slots, cursor);
        
        assertEquals(2, result.size()); // Only slots 0 and 2 are valid
        assertTrue(result.containsKey(0));
        assertFalse(result.containsKey(1)); // Skipped because different type
        assertTrue(result.containsKey(2));
        assertEquals(7, result.get(0).getAmount()); // 5 + 2
        assertEquals(2, result.get(2).getAmount()); // 0 + 2
        assertEquals(2, cursor.getAmount()); // 6 - 2 - 2 = 2 (only 2 slots were used)
    }
    
    @Test
    void simulateItemDrag_rightDrag_addsToExistingSimilarItems() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        top.setItem(0, ItemStack.of(Material.DIAMOND, 5));
        top.setItem(1, ItemStack.of(Material.DIAMOND, 3));
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 9);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.RIGHT, view, slots, cursor);
        
        assertEquals(3, result.size());
        // Each slot gets 1 item
        assertEquals(6, result.get(0).getAmount()); // 5 + 1
        assertEquals(4, result.get(1).getAmount()); // 3 + 1
        assertEquals(1, result.get(2).getAmount()); // 0 + 1
        assertEquals(6, cursor.getAmount()); // 9 - 3 = 6
    }
    
    @Test
    void simulateItemDrag_leftDrag_moreSlotsThanCursorItems() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 3);
        IntList slots = new IntArrayList(new int[] {0, 1, 2, 3, 4, 5});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.LEFT, view, slots, cursor);
        
        // With 3 items and 6 slots, each slot gets 0 items (integer division 3/6=0)
        // The implementation still creates entries but with 0 amount each
        assertEquals(6, result.size());
        for (int i = 0; i < 6; i++) {
            assertEquals(0, result.get(i).getAmount());
        }
        assertEquals(3, cursor.getAmount()); // Cursor unchanged
    }
    
    @Test
    void simulateItemDrag_leftDrag_respectsMaxStackSize() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        top.setItem(0, ItemStack.of(Material.DIAMOND, 60)); // Near max (64)
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 20);
        IntList slots = new IntArrayList(new int[] {0, 1});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.LEFT, view, slots, cursor);
        
        assertEquals(2, result.size());
        // 10 items per slot, but slot 0 can only take 4 more (64 - 60 = 4)
        assertEquals(64, result.get(0).getAmount()); // Capped at max
        assertEquals(10, result.get(1).getAmount());
        assertEquals(6, cursor.getAmount()); // 20 - 4 - 10 = 6
    }
    
    @Test
    void simulateItemDrag_rightDrag_respectsMaxStackSize() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        top.setItem(0, ItemStack.of(Material.DIAMOND, 64)); // Already at max
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 9);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.RIGHT, view, slots, cursor);
        
        assertEquals(2, result.size()); // Slot 0 is skipped (already full)
        assertFalse(result.containsKey(0));
        assertEquals(1, result.get(1).getAmount());
        assertEquals(1, result.get(2).getAmount());
        assertEquals(7, cursor.getAmount()); // 9 - 2 = 7
    }
    
    @Test
    void simulateItemDrag_middleDrag() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 5);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.MIDDLE, view, slots, cursor);
        
        // Middle drag in creative mode sets slots to max stack size
        assertTrue(result.isEmpty()); // Returns empty because it directly modifies view
        assertEquals(64, view.getItem(0).getAmount());
        assertEquals(64, view.getItem(1).getAmount());
        assertEquals(64, view.getItem(2).getAmount());
        assertEquals(0, cursor.getAmount());
    }
    
    @Test
    void simulateItemDrag_leftDrag_unevenDistribution() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 10);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.LEFT, view, slots, cursor);
        
        assertEquals(3, result.size());
        // 10 / 3 = 3 per slot, 1 remains
        assertEquals(3, result.get(0).getAmount());
        assertEquals(3, result.get(1).getAmount());
        assertEquals(3, result.get(2).getAmount());
        assertEquals(1, cursor.getAmount()); // Remainder
    }
    
    @Test
    void simulateItemDrag_rightDrag_cursorRunsOut() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 2);
        IntList slots = new IntArrayList(new int[] {0, 1, 2, 3, 4});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.RIGHT, view, slots, cursor);
        
        assertEquals(2, result.size()); // Only 2 slots filled
        assertEquals(1, result.get(0).getAmount());
        assertEquals(1, result.get(1).getAmount());
        assertFalse(result.containsKey(2));
        assertEquals(0, cursor.getAmount());
    }
    
    @Test
    void simulateItemDrag_leftDrag_singleSlot() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 10);
        IntList slots = new IntArrayList(new int[] {0});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.LEFT, view, slots, cursor);
        
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getAmount());
        assertEquals(0, cursor.getAmount());
    }
    
    @Test
    void simulateItemDrag_rightDrag_skipsDifferentItemTypes() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        top.setItem(1, ItemStack.of(Material.EMERALD, 3)); // Different type
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 9);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        var result = InventoryUtils.simulateItemDrag(ClickType.RIGHT, view, slots, cursor);
        
        assertEquals(2, result.size());
        assertTrue(result.containsKey(0));
        assertFalse(result.containsKey(1)); // Skipped
        assertTrue(result.containsKey(2));
        assertEquals(1, result.get(0).getAmount());
        assertEquals(1, result.get(2).getAmount());
        assertEquals(7, cursor.getAmount()); // 9 - 2 = 7
    }
    
    @Test
    void simulateItemDrag_leftDrag_emptySlotsList() {
        var player = server.addPlayer();
        var top = Bukkit.createInventory(null, 9);
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 10);
        IntList slots = new IntArrayList(new int[] {});
        
        // Empty slots list should be handled gracefully
        var result = InventoryUtils.simulateItemDrag(ClickType.LEFT, view, slots, cursor);
        
        assertTrue(result.isEmpty());
        assertEquals(10, cursor.getAmount()); // Unchanged
    }
    
    @Test
    void simulateItemDrag_middleDrag_addsToExistingSimilarItems() {
        var player = server.addPlayer();
        player.setGameMode(GameMode.CREATIVE);
        var top = Bukkit.createInventory(null, 9);
        top.setItem(0, ItemStack.of(Material.DIAMOND, 10));
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 5);
        IntList slots = new IntArrayList(new int[] {0, 1});
        
        InventoryUtils.simulateItemDrag(ClickType.MIDDLE, view, slots, cursor);
        
        // Middle drag in creative sets slots to max stack size
        assertEquals(64, view.getItem(0).getAmount());
        assertEquals(64, view.getItem(1).getAmount());
        assertEquals(0, cursor.getAmount());
    }
    
    @Test
    void simulateItemDrag_middleDrag_skipsDifferentItemTypes() {
        var player = server.addPlayer();
        player.setGameMode(GameMode.CREATIVE);
        var top = Bukkit.createInventory(null, 9);
        top.setItem(1, ItemStack.of(Material.EMERALD, 10)); // Different type
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 5);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        InventoryUtils.simulateItemDrag(ClickType.MIDDLE, view, slots, cursor);
        
        assertEquals(64, view.getItem(0).getAmount()); // Filled
        assertEquals(Material.EMERALD, view.getItem(1).getType()); // Unchanged (different type)
        assertEquals(10, view.getItem(1).getAmount());
        assertEquals(64, view.getItem(2).getAmount()); // Filled
        assertEquals(0, cursor.getAmount());
    }
    
    @Test
    void simulateItemDrag_middleDrag_skipsFullSlots() {
        var player = server.addPlayer();
        player.setGameMode(GameMode.CREATIVE);
        var top = Bukkit.createInventory(null, 9);
        top.setItem(1, ItemStack.of(Material.DIAMOND, 64)); // Already at max
        var view = new FakeInventoryView(player, top);
        var cursor = ItemStack.of(Material.DIAMOND, 5);
        IntList slots = new IntArrayList(new int[] {0, 1, 2});
        
        InventoryUtils.simulateItemDrag(ClickType.MIDDLE, view, slots, cursor);
        
        assertEquals(64, view.getItem(0).getAmount());
        assertEquals(64, view.getItem(1).getAmount()); // Was already 64
        assertEquals(64, view.getItem(2).getAmount());
        assertEquals(0, cursor.getAmount());
    }
    
}