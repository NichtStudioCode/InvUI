package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.InvUI;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryAdapterTest {
    
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
    void getSize() {
        var backingInv = new VirtualInventory(10);
        var adapter = new InventoryAdapter(backingInv);
        
        assertEquals(10, adapter.getSize());
        
        var backingInv2 = new VirtualInventory(27);
        var adapter2 = new InventoryAdapter(backingInv2);
        
        assertEquals(27, adapter2.getSize());
    }
    
    @Test
    void getMaxStackSize() {
        var backingInv = new VirtualInventory(10);
        var adapter = new InventoryAdapter(backingInv);
        
        // Default max stack size is 64
        assertEquals(64, adapter.getMaxStackSize());
        
        // With custom max stack sizes
        var backingInv2 = new VirtualInventory(3, null, new int[]{32, 16, 99});
        var adapter2 = new InventoryAdapter(backingInv2);
        
        // Should return the maximum of all slot stack sizes
        assertEquals(99, adapter2.getMaxStackSize());
    }
    
    @Test
    void setMaxStackSize() {
        var backingInv = new VirtualInventory(10);
        var adapter = new InventoryAdapter(backingInv);
        
        assertEquals(64, adapter.getMaxStackSize());
        
        adapter.setMaxStackSize(32);
        
        assertEquals(32, adapter.getMaxStackSize());
        
        // Verify all slots have the new max stack size
        for (int maxStackSize : backingInv.getMaxStackSizes()) {
            assertEquals(32, maxStackSize);
        }
    }
    
    @Test
    void getItem() {
        var backingInv = new VirtualInventory(10);
        var adapter = new InventoryAdapter(backingInv);
        
        assertNull(adapter.getItem(0));
        assertNull(adapter.getItem(1));
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT));
        
        assertEquals(ItemStack.of(Material.DIRT), adapter.getItem(0));
        assertNull(adapter.getItem(1));
    }
    
    @Test
    void setItem() {
        var backingInv = new VirtualInventory(10);
        var adapter = new InventoryAdapter(backingInv);
        
        assertNull(adapter.getItem(0));
        
        adapter.setItem(0, ItemStack.of(Material.DIAMOND));
        
        assertEquals(ItemStack.of(Material.DIAMOND), adapter.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND), backingInv.getItem(0));
        
        // Setting null should clear the slot
        adapter.setItem(0, null);
        assertNull(adapter.getItem(0));
        assertNull(backingInv.getItem(0));
    }
    
    @Test
    void addItem() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        // Add item to empty inventory - should succeed with no leftovers
        var result = adapter.addItem(ItemStack.of(Material.DIRT, 32));
        
        assertTrue(result.isEmpty());
        assertEquals(ItemStack.of(Material.DIRT, 32), adapter.getItem(0));
    }
    
    @Test
    void addItem_fillsPartialStacksFirst() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        // Pre-fill with partial stack
        backingInv.setItem(null, 1, ItemStack.of(Material.DIRT, 32));
        
        // Add more dirt - should fill existing stack first
        var result = adapter.addItem(ItemStack.of(Material.DIRT, 20));
        
        assertTrue(result.isEmpty());
        assertNull(adapter.getItem(0)); // slot 0 should stay empty
        assertEquals(ItemStack.of(Material.DIRT, 52), adapter.getItem(1)); // 32 + 20 = 52
    }
    
    @Test
    void addItem_splitsAcrossMultipleSlots() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        // Add more than max stack size - should split across slots
        var result = adapter.addItem(ItemStack.of(Material.DIRT, 100));
        
        assertTrue(result.isEmpty());
        assertEquals(ItemStack.of(Material.DIRT, 64), adapter.getItem(0)); // max stack
        assertEquals(ItemStack.of(Material.DIRT, 36), adapter.getItem(1)); // remainder
    }
    
    @Test
    void addItem_returnsLeftoversWhenFull() {
        var backingInv = new VirtualInventory(2);
        var adapter = new InventoryAdapter(backingInv);
        
        // Fill inventory
        backingInv.setItem(null, 0, ItemStack.of(Material.STONE, 64));
        backingInv.setItem(null, 1, ItemStack.of(Material.STONE, 64));
        
        // Try to add dirt - no room
        var result = adapter.addItem(ItemStack.of(Material.DIRT, 32));
        
        assertEquals(1, result.size());
        assertEquals(ItemStack.of(Material.DIRT, 32), result.get(0));
    }
    
    @Test
    void addItem_partiallyFitsReturnsRemainder() {
        var backingInv = new VirtualInventory(2);
        var adapter = new InventoryAdapter(backingInv);
        
        // Fill slot 0, leave slot 1 with partial stack
        backingInv.setItem(null, 0, ItemStack.of(Material.STONE, 64));
        backingInv.setItem(null, 1, ItemStack.of(Material.DIRT, 50));
        
        // Try to add 30 dirt - only 14 will fit (64 - 50)
        var result = adapter.addItem(ItemStack.of(Material.DIRT, 30));
        
        assertEquals(1, result.size());
        assertEquals(ItemStack.of(Material.DIRT, 16), result.get(0)); // 30 - 14 = 16
        assertEquals(ItemStack.of(Material.DIRT, 64), adapter.getItem(1)); // now full
    }
    
    @Test
    void addItem_multipleItems() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        // Add multiple items at once
        var result = adapter.addItem(
            ItemStack.of(Material.DIRT, 32),
            ItemStack.of(Material.STONE, 16),
            ItemStack.of(Material.DIAMOND, 5)
        );
        
        assertTrue(result.isEmpty());
        assertEquals(ItemStack.of(Material.DIRT, 32), adapter.getItem(0));
        assertEquals(ItemStack.of(Material.STONE, 16), adapter.getItem(1));
        assertEquals(ItemStack.of(Material.DIAMOND, 5), adapter.getItem(2));
    }
    
    @Test
    void addItem_multipleItemsSomeDoNotFit() {
        var backingInv = new VirtualInventory(2);
        var adapter = new InventoryAdapter(backingInv);
        
        // Add multiple items - only first two fit
        var result = adapter.addItem(
            ItemStack.of(Material.DIRT, 64),
            ItemStack.of(Material.STONE, 64),
            ItemStack.of(Material.DIAMOND, 5)
        );
        
        assertEquals(1, result.size());
        assertEquals(ItemStack.of(Material.DIAMOND, 5), result.get(2)); // index 2 didn't fit
    }
    
    @Test
    void addItem_throwsOnNullItems() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        assertThrows(IllegalArgumentException.class, () -> 
            adapter.addItem((ItemStack[]) null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            adapter.addItem(ItemStack.of(Material.DIRT), null));
    }
    
    @Test
    void removeItem() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 64));
        backingInv.setItem(null, 1, ItemStack.of(Material.STONE, 32));
        
        // Remove some dirt
        var result = adapter.removeItem(ItemStack.of(Material.DIRT, 32));
        
        assertTrue(result.isEmpty());
        assertEquals(ItemStack.of(Material.DIRT, 32), adapter.getItem(0)); // 64 - 32 = 32
        assertEquals(ItemStack.of(Material.STONE, 32), adapter.getItem(1)); // unchanged
    }
    
    @Test
    void removeItem_removesFromMultipleStacks() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        // Set up multiple dirt stacks
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 4, ItemStack.of(Material.DIRT, 32));
        
        // Remove 50 dirt - should take from multiple stacks
        // Takes 32 from slot 0, then 18 from slot 2 (32+18=50)
        var result = adapter.removeItem(ItemStack.of(Material.DIRT, 50));
        
        assertTrue(result.isEmpty());
        assertNull(adapter.getItem(0)); // emptied (32 taken)
        assertEquals(ItemStack.of(Material.DIRT, 14), adapter.getItem(2)); // 32 - 18 = 14 left
        assertEquals(ItemStack.of(Material.DIRT, 32), adapter.getItem(4)); // untouched
    }
    
    @Test
    void removeItem_removesEntireStack() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 32));
        
        // Remove exact amount
        var result = adapter.removeItem(ItemStack.of(Material.DIRT, 32));
        
        assertTrue(result.isEmpty());
        assertNull(adapter.getItem(0)); // slot should be empty
    }
    
    @Test
    void removeItem_returnsLeftoversWhenNotEnough() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 20));
        
        // Try to remove more than available
        var result = adapter.removeItem(ItemStack.of(Material.DIRT, 50));
        
        assertEquals(1, result.size());
        assertEquals(ItemStack.of(Material.DIRT, 30), result.get(0)); // 50 - 20 = 30 couldn't be removed
        assertNull(adapter.getItem(0)); // all removed
    }
    
    @Test
    void removeItem_noMatchingItems() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.STONE, 64));
        
        // Try to remove dirt from inventory with only stone
        var result = adapter.removeItem(ItemStack.of(Material.DIRT, 32));
        
        assertEquals(1, result.size());
        assertEquals(ItemStack.of(Material.DIRT, 32), result.get(0)); // nothing removed
        assertEquals(ItemStack.of(Material.STONE, 64), adapter.getItem(0)); // unchanged
    }
    
    @Test
    void removeItem_multipleItems() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 64));
        backingInv.setItem(null, 1, ItemStack.of(Material.STONE, 64));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIAMOND, 10));
        
        // Remove multiple items at once
        var result = adapter.removeItem(
            ItemStack.of(Material.DIRT, 32),
            ItemStack.of(Material.STONE, 16),
            ItemStack.of(Material.DIAMOND, 5)
        );
        
        assertTrue(result.isEmpty());
        assertEquals(ItemStack.of(Material.DIRT, 32), adapter.getItem(0));
        assertEquals(ItemStack.of(Material.STONE, 48), adapter.getItem(1));
        assertEquals(ItemStack.of(Material.DIAMOND, 5), adapter.getItem(2));
    }
    
    @Test
    void removeItem_multipleItemsSomeNotAvailable() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 64));
        
        // Remove multiple items - only dirt exists
        var result = adapter.removeItem(
            ItemStack.of(Material.DIRT, 32),
            ItemStack.of(Material.STONE, 16)
        );
        
        assertEquals(1, result.size());
        assertEquals(ItemStack.of(Material.STONE, 16), result.get(1)); // stone not found
        assertEquals(ItemStack.of(Material.DIRT, 32), adapter.getItem(0)); // dirt was removed
    }
    
    @Test
    void removeItem_emptyInventory() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        // Try to remove from empty inventory
        var result = adapter.removeItem(ItemStack.of(Material.DIRT, 32));
        
        assertEquals(1, result.size());
        assertEquals(ItemStack.of(Material.DIRT, 32), result.get(0));
    }
    
    @Test
    void removeItem_throwsOnNullArray() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        assertThrows(IllegalArgumentException.class, () -> 
            adapter.removeItem((ItemStack[]) null));
    }
    
    @Test
    void removeItem_skipsNullElements() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 64));
        
        // Null elements should be skipped
        var result = adapter.removeItem(null, ItemStack.of(Material.DIRT, 32));
        
        assertTrue(result.isEmpty());
        assertEquals(ItemStack.of(Material.DIRT, 32), adapter.getItem(0));
    }
    
    @Test
    void getContents() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        // Initially all null
        var contents = adapter.getContents();
        assertEquals(3, contents.length);
        assertNull(contents[0]);
        assertNull(contents[1]);
        assertNull(contents[2]);
        
        // Set some items
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT));
        backingInv.setItem(null, 2, ItemStack.of(Material.STONE, 16));
        
        contents = adapter.getContents();
        assertEquals(ItemStack.of(Material.DIRT), contents[0]);
        assertNull(contents[1]);
        assertEquals(ItemStack.of(Material.STONE, 16), contents[2]);
    }
    
    @Test
    void setContents() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        ItemStack[] newContents = new ItemStack[]{
            ItemStack.of(Material.DIRT),
            null,
            ItemStack.of(Material.DIAMOND, 5)
        };
        
        adapter.setContents(newContents);
        
        assertEquals(ItemStack.of(Material.DIRT), adapter.getItem(0));
        assertNull(adapter.getItem(1));
        assertEquals(ItemStack.of(Material.DIAMOND, 5), adapter.getItem(2));
        
        // Test with smaller array
        adapter.setContents(new ItemStack[]{ItemStack.of(Material.GOLD_INGOT)});
        assertEquals(ItemStack.of(Material.GOLD_INGOT), adapter.getItem(0));
        
        // Test with array larger than inventory size throws exception
        assertThrows(IllegalArgumentException.class, () -> 
            adapter.setContents(new ItemStack[10]));
    }
    
    @Test
    void getStorageContents() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT));
        backingInv.setItem(null, 2, ItemStack.of(Material.STONE));
        
        // For this adapter, storage contents equals contents
        var storageContents = adapter.getStorageContents();
        var contents = adapter.getContents();
        
        assertArrayEquals(contents, storageContents);
    }
    
    @Test
    void setStorageContents() {
        var backingInv = new VirtualInventory(3);
        var adapter = new InventoryAdapter(backingInv);
        
        ItemStack[] newContents = new ItemStack[]{
            ItemStack.of(Material.IRON_INGOT),
            ItemStack.of(Material.GOLD_INGOT),
            null
        };
        
        adapter.setStorageContents(newContents);
        
        // setStorageContents delegates to setContents
        assertEquals(ItemStack.of(Material.IRON_INGOT), adapter.getItem(0));
        assertEquals(ItemStack.of(Material.GOLD_INGOT), adapter.getItem(1));
        assertNull(adapter.getItem(2));
    }
    
    @Test
    void testContainsMaterial() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        // Empty inventory contains nothing
        assertFalse(adapter.contains(Material.DIRT));
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT));
        backingInv.setItem(null, 2, ItemStack.of(Material.STONE, 32));
        
        assertTrue(adapter.contains(Material.DIRT));
        assertTrue(adapter.contains(Material.STONE));
        assertFalse(adapter.contains(Material.DIAMOND));
    }
    
    @Test
    void containsItemStack() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        var dirtStack = ItemStack.of(Material.DIRT, 16);
        
        // Empty inventory
        assertFalse(adapter.contains(dirtStack));
        assertFalse(adapter.contains((ItemStack) null));
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 16));
        
        // Exact match (type and amount)
        assertTrue(adapter.contains(ItemStack.of(Material.DIRT, 16)));
        
        // Different amount - should not match
        assertFalse(adapter.contains(ItemStack.of(Material.DIRT, 32)));
        
        // Different type
        assertFalse(adapter.contains(ItemStack.of(Material.STONE, 16)));
    }
    
    @Test
    void containsMaterialCount() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT, 16));
        
        // Total 48 dirt items (counts stacks, not amount)
        assertTrue(adapter.contains(Material.DIRT, 1));
        assertTrue(adapter.contains(Material.DIRT, 2)); // 2 stacks of dirt
        assertFalse(adapter.contains(Material.DIRT, 3)); // only 2 stacks
        
        assertFalse(adapter.contains(Material.STONE, 1));
    }
    
    @Test
    void testContainsItemStackCount() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 1, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT, 16));
        
        var dirtStack32 = ItemStack.of(Material.DIRT, 32);
        
        // Counts exact matching stacks (type AND amount)
        assertTrue(adapter.contains(dirtStack32, 1));
        assertTrue(adapter.contains(dirtStack32, 2)); // 2 stacks with exactly 32 dirt
        assertFalse(adapter.contains(dirtStack32, 3)); // only 2 stacks with exactly 32
        
        // Null item returns false
        assertFalse(adapter.contains((ItemStack) null, 5));
    }
    
    @Test
    void containsAtLeast() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT, 16));
        
        // Total of 48 dirt across all slots
        assertTrue(adapter.containsAtLeast(ItemStack.of(Material.DIRT), 1));
        assertTrue(adapter.containsAtLeast(ItemStack.of(Material.DIRT), 48));
        assertFalse(adapter.containsAtLeast(ItemStack.of(Material.DIRT), 49));
        
        // Different material
        assertFalse(adapter.containsAtLeast(ItemStack.of(Material.STONE), 1));
        
        // Null item returns false
        assertFalse(adapter.containsAtLeast(null, 1));
    }
    
    @Test
    void containsAtLeast_splitAcrossManyStacks() {
        var backingInv = new VirtualInventory(10);
        var adapter = new InventoryAdapter(backingInv);
        
        // Split items across many slots
        backingInv.setItem(null, 0, ItemStack.of(Material.DIAMOND, 10));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIAMOND, 15));
        backingInv.setItem(null, 5, ItemStack.of(Material.DIAMOND, 20));
        backingInv.setItem(null, 7, ItemStack.of(Material.DIAMOND, 5));
        // Total: 50 diamonds
        
        assertTrue(adapter.containsAtLeast(ItemStack.of(Material.DIAMOND), 50));
        assertTrue(adapter.containsAtLeast(ItemStack.of(Material.DIAMOND), 25)); // partial
        assertFalse(adapter.containsAtLeast(ItemStack.of(Material.DIAMOND), 51));
    }
    
    @Test
    void containsAtLeast_withMixedMaterials() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        // Mix of materials
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 1, ItemStack.of(Material.STONE, 64));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT, 16));
        
        // Only counts similar items
        assertTrue(adapter.containsAtLeast(ItemStack.of(Material.DIRT), 48));
        assertTrue(adapter.containsAtLeast(ItemStack.of(Material.STONE), 64));
        assertFalse(adapter.containsAtLeast(ItemStack.of(Material.DIRT), 49));
    }
    
    @Test
    void containsAtLeast_emptyInventory() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        assertFalse(adapter.containsAtLeast(ItemStack.of(Material.DIRT), 1));
    }
    
    @Test
    void allMaterial() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT, 16));
        backingInv.setItem(null, 4, ItemStack.of(Material.STONE, 8));
        
        var allDirt = adapter.all(Material.DIRT);
        
        assertEquals(2, allDirt.size());
        assertEquals(ItemStack.of(Material.DIRT, 32), allDirt.get(0));
        assertEquals(ItemStack.of(Material.DIRT, 16), allDirt.get(2));
        assertNull(allDirt.get(4));
        
        var allStone = adapter.all(Material.STONE);
        assertEquals(1, allStone.size());
        assertEquals(ItemStack.of(Material.STONE, 8), allStone.get(4));
        
        // Non-existent material returns empty map
        var allDiamond = adapter.all(Material.DIAMOND);
        assertTrue(allDiamond.isEmpty());
    }
    
    @Test
    void allItemStack() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 3, ItemStack.of(Material.DIRT, 16));
        
        // all(ItemStack) matches type AND amount
        var exactMatch = adapter.all(ItemStack.of(Material.DIRT, 32));
        
        assertEquals(2, exactMatch.size());
        assertEquals(ItemStack.of(Material.DIRT, 32), exactMatch.get(0));
        assertEquals(ItemStack.of(Material.DIRT, 32), exactMatch.get(2));
        assertNull(exactMatch.get(3)); // has 16, not 32
        
        // Different amount returns different results
        var smallerMatch = adapter.all(ItemStack.of(Material.DIRT, 16));
        assertEquals(1, smallerMatch.size());
        assertEquals(ItemStack.of(Material.DIRT, 16), smallerMatch.get(3));
    }
    
    @Test
    void firstMaterial() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        // Empty inventory returns -1
        assertEquals(-1, adapter.first(Material.DIRT));
        
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT, 16));
        backingInv.setItem(null, 4, ItemStack.of(Material.DIRT, 32));
        
        // Returns first slot with matching material
        assertEquals(2, adapter.first(Material.DIRT));
        
        // Non-existent material
        assertEquals(-1, adapter.first(Material.DIAMOND));
    }
    
    @Test
    void firstItemStack() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 1, ItemStack.of(Material.DIRT, 16));
        backingInv.setItem(null, 3, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 4, ItemStack.of(Material.DIRT, 32));
        
        // Matches type AND amount
        assertEquals(3, adapter.first(ItemStack.of(Material.DIRT, 32)));
        assertEquals(1, adapter.first(ItemStack.of(Material.DIRT, 16)));
        
        // No exact match returns -1
        assertEquals(-1, adapter.first(ItemStack.of(Material.DIRT, 64)));
        assertEquals(-1, adapter.first(ItemStack.of(Material.STONE, 16)));
    }
    
    @Test
    void firstEmpty() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        // Empty inventory - first empty is 0
        assertEquals(0, adapter.firstEmpty());
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT));
        backingInv.setItem(null, 1, ItemStack.of(Material.STONE));
        
        // First empty is now 2
        assertEquals(2, adapter.firstEmpty());
        
        // Fill all slots
        for (int i = 0; i < 5; i++) {
            backingInv.setItem(null, i, ItemStack.of(Material.DIRT));
        }
        
        // No empty slots returns -1
        assertEquals(-1, adapter.firstEmpty());
    }
    
    @Test
    void isEmpty() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        // Initially empty
        assertTrue(adapter.isEmpty());
        
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT));
        
        // Not empty anymore
        assertFalse(adapter.isEmpty());
        
        backingInv.setItem(null, 2, null);
        
        // Empty again
        assertTrue(adapter.isEmpty());
    }
    
    @Test
    void removeMaterial() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT, 16));
        backingInv.setItem(null, 3, ItemStack.of(Material.STONE, 8));
        
        // Remove all dirt
        adapter.remove(Material.DIRT);
        
        assertNull(adapter.getItem(0));
        assertNull(adapter.getItem(2));
        assertEquals(ItemStack.of(Material.STONE, 8), adapter.getItem(3));
    }
    
    @Test
    void removeItemStack() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT, 32));
        backingInv.setItem(null, 1, ItemStack.of(Material.DIRT, 16));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIRT, 32));
        
        // Remove only stacks matching exactly (type AND amount)
        adapter.remove(ItemStack.of(Material.DIRT, 32));
        
        assertNull(adapter.getItem(0));
        assertEquals(ItemStack.of(Material.DIRT, 16), adapter.getItem(1)); // 16 not removed
        assertNull(adapter.getItem(2));
    }
    
    @Test
    void clearSlot() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT));
        backingInv.setItem(null, 2, ItemStack.of(Material.STONE));
        
        // Clear specific slot
        adapter.clear(0);
        
        assertNull(adapter.getItem(0));
        assertEquals(ItemStack.of(Material.STONE), adapter.getItem(2));
    }
    
    @Test
    void cleatEntire() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT));
        backingInv.setItem(null, 2, ItemStack.of(Material.STONE));
        backingInv.setItem(null, 4, ItemStack.of(Material.DIAMOND));
        
        assertFalse(adapter.isEmpty());
        
        // Clear entire inventory
        adapter.clear();
        
        assertTrue(adapter.isEmpty());
        for (int i = 0; i < 5; i++) {
            assertNull(adapter.getItem(i));
        }
    }
    
    @Test
    void getType() {
        var backingInv = new VirtualInventory(10);
        var adapter = new InventoryAdapter(backingInv);
        
        // Always returns CHEST for this adapter
        assertEquals(InventoryType.CHEST, adapter.getType());
    }
    
    @Test
    void iterator() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT));
        backingInv.setItem(null, 2, ItemStack.of(Material.STONE));
        backingInv.setItem(null, 4, ItemStack.of(Material.DIAMOND));
        
        var iterator = adapter.iterator();
        List<ItemStack> items = new ArrayList<>();
        while (iterator.hasNext()) {
            items.add(iterator.next());
        }
        
        assertEquals(5, items.size());
        assertEquals(ItemStack.of(Material.DIRT), items.get(0));
        assertNull(items.get(1));
        assertEquals(ItemStack.of(Material.STONE), items.get(2));
        assertNull(items.get(3));
        assertEquals(ItemStack.of(Material.DIAMOND), items.get(4));
    }
    
    @Test
    void iteratorIndex() {
        var backingInv = new VirtualInventory(5);
        var adapter = new InventoryAdapter(backingInv);
        
        backingInv.setItem(null, 0, ItemStack.of(Material.DIRT));
        backingInv.setItem(null, 1, ItemStack.of(Material.STONE));
        backingInv.setItem(null, 2, ItemStack.of(Material.DIAMOND));
        backingInv.setItem(null, 3, ItemStack.of(Material.GOLD_INGOT));
        backingInv.setItem(null, 4, ItemStack.of(Material.IRON_INGOT));
        
        // Start at index 2
        var iterator = adapter.iterator(2);
        
        // First call to next() returns item at index 2
        assertTrue(iterator.hasNext());
        assertEquals(ItemStack.of(Material.DIAMOND), iterator.next());
        assertEquals(ItemStack.of(Material.GOLD_INGOT), iterator.next());
        assertEquals(ItemStack.of(Material.IRON_INGOT), iterator.next());
        assertFalse(iterator.hasNext());
        
        // Can go backwards with previous()
        assertEquals(ItemStack.of(Material.IRON_INGOT), iterator.previous());
        assertEquals(ItemStack.of(Material.GOLD_INGOT), iterator.previous());
    }
}