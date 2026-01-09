package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockbukkit.mockbukkit.MockBukkit;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {
    
    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
        InvUI.getInstance().setExceptionHandler((msg, t) -> {
            throw new AssertionError(msg, t);
        });
    }
    
    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
    
    private static Stream<Arguments> updateReasons() {
        var invWithNoopHandlers = new VirtualInventory(5);
        invWithNoopHandlers.addPostUpdateHandler(x -> {});
        invWithNoopHandlers.addPreUpdateHandler(x -> {});
        
        return Stream.of(
            Arguments.of(
                Named.of("inv w/ handlers", new VirtualInventory(invWithNoopHandlers)),
                Named.of("null", (UpdateReason) null)
            ),
            Arguments.of(
                Named.of("inv w/o handlers", new VirtualInventory(5)),
                Named.of("SUPPRESSED", UpdateReason.SUPPRESSED)
            ),
            Arguments.of(
                Named.of("inv w/ handlers", new VirtualInventory(invWithNoopHandlers)),
                Named.of("custom", new UpdateReason() {})
            )
        );
    }
    
    private static Stream<Arguments> unsuppressedUpdateReasons() {
        var invWithNoopHandlers = new VirtualInventory(5);
        invWithNoopHandlers.addPostUpdateHandler(x -> {});
        invWithNoopHandlers.addPreUpdateHandler(x -> {});
        
        return Stream.of(
            Arguments.of(
                Named.of("inv w/ handlers", new VirtualInventory(invWithNoopHandlers)),
                Named.of("null", (UpdateReason) null)
            ),
            Arguments.of(
                Named.of("inv w/ handlers", new VirtualInventory(invWithNoopHandlers)),
                Named.of("custom", new UpdateReason() {})
            )
        );
    }
    
    
    //<editor-fold desc="forceSetItem">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void forceSetItem_SetsItemInEmptySlot(VirtualInventory inv, UpdateReason updateReason) {
        var item = ItemStack.of(Material.DIAMOND, 32);
        
        boolean result = inv.forceSetItem(updateReason, 0, item);
        
        assertTrue(result);
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void forceSetItem_ReplacesExistingItem(VirtualInventory inv, UpdateReason updateReason) {
        inv.forceSetItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        
        boolean result = inv.forceSetItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertTrue(result);
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void forceSetItem_ClearsSlotWithNull(VirtualInventory inv, UpdateReason updateReason) {
        inv.forceSetItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        boolean result = inv.forceSetItem(updateReason, 0, null);
        
        assertTrue(result);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void forceSetItem_ClearsSlotWithEmptyItemStack(VirtualInventory inv, UpdateReason updateReason) {
        inv.forceSetItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        boolean result = inv.forceSetItem(updateReason, 0, ItemStack.empty());
        
        assertTrue(result);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void forceSetItem_IgnoresMaxStackSizeLimit(VirtualInventory inv, UpdateReason updateReason) {
        inv.setMaxStackSizes(new int[] {16, 64, 64, 64, 64});
        var item = ItemStack.of(Material.DIAMOND, 64);
        
        boolean result = inv.forceSetItem(updateReason, 0, item);
        
        assertTrue(result);
        assertEquals(ItemStack.of(Material.DIAMOND, 64), inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void forceSetItem_CallsPreAndPostUpdateEvents(VirtualInventory inv, UpdateReason updateReason) {
        AtomicBoolean preEventCalled = new AtomicBoolean(false);
        AtomicBoolean postEventCalled = new AtomicBoolean(false);
        
        inv.addPreUpdateHandler(event -> preEventCalled.set(true));
        inv.addPostUpdateHandler(event -> postEventCalled.set(true));
        
        inv.forceSetItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertTrue(preEventCalled.get());
        assertTrue(postEventCalled.get());
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void forceSetItem_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        boolean result = inv.forceSetItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertFalse(result);
        assertNull(inv.getItem(0));
    }
    
    @Test
    void forceSetItem_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        inv.forceSetItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertFalse(eventCalled.get());
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void forceSetItem_PreUpdateEventProvidesCorrectNewItem(VirtualInventory inv, UpdateReason updateReason) {
        AtomicReference<ItemStack> eventNewItem = new AtomicReference<>();
        inv.addPreUpdateHandler(event -> eventNewItem.set(event.getNewItem()));
        
        inv.forceSetItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertEquals(ItemStack.of(Material.DIAMOND, 32), eventNewItem.get());
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    //</editor-fold>
    
    //<editor-fold desc="setItem">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItem_SetsItemInEmptySlot(VirtualInventory inv, UpdateReason updateReason) {
        var item = ItemStack.of(Material.DIAMOND, 32);
        
        boolean result = inv.setItem(updateReason, 0, item);
        
        assertTrue(result);
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItem_RespectsSlotMaxStackSize(VirtualInventory inv, UpdateReason updateReason) {
        inv.setMaxStackSizes(new int[] {16, 64, 64, 64, 64});
        var item = ItemStack.of(Material.DIAMOND, 32);
        
        boolean result = inv.setItem(updateReason, 0, item);
        
        assertFalse(result);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItem_AllowsItemAtMaxStackSize(VirtualInventory inv, UpdateReason updateReason) {
        inv.setMaxStackSizes(new int[] {16, 64, 64, 64, 64});
        var item = ItemStack.of(Material.DIAMOND, 16);
        
        boolean result = inv.setItem(updateReason, 0, item);
        
        assertTrue(result);
        assertEquals(ItemStack.of(Material.DIAMOND, 16), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItem_ClearsSlotWithNull(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(null, 0, ItemStack.of(Material.DIAMOND, 32));
        
        boolean result = inv.setItem(updateReason, 0, null);
        
        assertTrue(result);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItem_ClearsSlotWithEmptyItemStack(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(null, 0, ItemStack.of(Material.DIAMOND, 32));
        
        boolean result = inv.setItem(updateReason, 0, ItemStack.empty());
        
        assertTrue(result);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItem_ReplacesExistingItem(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        
        boolean result = inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertTrue(result);
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void setItem_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        boolean result = inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertFalse(result);
        assertNull(inv.getItem(0));
    }
    
    @Test
    void setItem_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        inv.setItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertFalse(eventCalled.get());
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItem_RespectsItemMaxStackSize(VirtualInventory inv, UpdateReason updateReason) {
        // Ender pearls have a max stack size of 16, oversized stacks should be rejected
        var item = ItemStack.of(Material.ENDER_PEARL, 32);
        
        boolean result = inv.setItem(updateReason, 0, item);
        
        assertFalse(result);
        assertNull(inv.getItem(0));
    }
    //</editor-fold>
    
    //<editor-fold desc="changeItem">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void changeItem_SetsItemInEmptySlot(VirtualInventory inv, UpdateReason updateReason) {
        ItemStack result = inv.changeItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertEquals(ItemStack.of(Material.DIAMOND, 32), result);
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void changeItem_ReturnsCurrentItemWhenExceedsMaxStackSize(VirtualInventory inv, UpdateReason updateReason) {
        inv.setMaxStackSizes(new int[] {16, 64, 64, 64, 64});
        inv.forceSetItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        
        ItemStack result = inv.changeItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertEquals(ItemStack.of(Material.STONE, 10), result);
        assertEquals(ItemStack.of(Material.STONE, 10), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void changeItem_ClearsSlotWithNull(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        ItemStack result = inv.changeItem(updateReason, 0, (ItemStack) null);
        
        assertNull(result);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void changeItem_ClearsSlotEmptyItemStack(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        ItemStack result = inv.changeItem(updateReason, 0, ItemStack.empty());
        
        assertNull(result);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void changeItem_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        ItemStack result = inv.changeItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertEquals(ItemStack.of(Material.STONE, 10), result);
        assertEquals(ItemStack.of(Material.STONE, 10), inv.getItem(0));
    }
    
    @Test
    void changeItem_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        inv.changeItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertFalse(eventCalled.get());
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void changeItemFunction_ReplacesItem(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        
        boolean result = inv.changeItem(updateReason, 0, current -> ItemStack.of(Material.DIAMOND, 32));
        
        assertTrue(result);
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void changeItemFunction_ReceivesCurrentItem(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        AtomicReference<ItemStack> receivedItem = new AtomicReference<>();
        
        inv.changeItem(updateReason, 0, current -> {
            receivedItem.set(current);
            return current;
        });
        
        assertEquals(ItemStack.of(Material.STONE, 10), receivedItem.get());
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void changeItemFunction_ClearsSlotWhenReturningNull(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        
        boolean result = inv.changeItem(updateReason, 0, current -> null);
        
        assertTrue(result);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void changeItemFunction_FailsWhenExceedsMaxStackSize(VirtualInventory inv, UpdateReason updateReason) {
        inv.setMaxStackSizes(new int[] {16, 64, 64, 64, 64});
        
        boolean result = inv.changeItem(updateReason, 0, current -> ItemStack.of(Material.DIAMOND, 32));
        
        assertFalse(result);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void changeItemFunction_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        boolean result = inv.changeItem(updateReason, 0, current -> ItemStack.of(Material.DIAMOND));
        
        assertFalse(result);
        assertNull(inv.getItem(0));
    }
    //</editor-fold>
    
    //<editor-fold desc="modifyItem">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void modifyItem_ModifiesExistingItem(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        boolean result = inv.modifyItem(updateReason, 0, item -> {
            if (item != null) item.setAmount(20);
        });
        
        assertTrue(result);
        assertEquals(ItemStack.of(Material.DIAMOND, 20), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void modifyItem_ReceivesNullForEmptySlot(VirtualInventory inv, UpdateReason updateReason) {
        AtomicReference<ItemStack> receivedItem = new AtomicReference<>();
        
        inv.modifyItem(updateReason, 0, receivedItem::set);
        
        assertNull(receivedItem.get());
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void modifyItem_FailsWhenExceedsMaxStackSize(VirtualInventory inv, UpdateReason updateReason) {
        inv.setMaxStackSizes(new int[] {16, 64, 64, 64, 64});
        inv.forceSetItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        boolean result = inv.modifyItem(updateReason, 0, item -> {
            if (item != null) item.setAmount(32);
        });
        
        assertFalse(result);
        assertEquals(ItemStack.of(Material.DIAMOND, 10), inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void modifyItem_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 1));
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        boolean result = inv.modifyItem(updateReason, 0, item -> {
            if (item != null) item.setAmount(2);
        });
        
        assertFalse(result);
        assertEquals(ItemStack.of(Material.DIAMOND, 1), inv.getItem(0));
    }
    
    @Test
    void modifyItem_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        inv.setItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 10));
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        inv.modifyItem(UpdateReason.SUPPRESSED, 0, item -> {
            if (item != null) item.setAmount(20);
        });
        
        assertFalse(eventCalled.get());
        assertEquals(ItemStack.of(Material.DIAMOND, 20), inv.getItem(0));
    }
    //</editor-fold>
    
    //<editor-fold desc="putItem">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void putItem_AddsToEmptySlot(VirtualInventory inv, UpdateReason updateReason) {
        int remaining = inv.putItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertEquals(0, remaining);
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void putItem_StacksWithSimilarItem(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 20));
        
        int remaining = inv.putItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        assertEquals(0, remaining);
        assertEquals(ItemStack.of(Material.DIAMOND, 30), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void putItem_ReturnsRemainingWhenFull(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 60));
        
        int remaining = inv.putItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        assertEquals(6, remaining);
        assertEquals(ItemStack.of(Material.DIAMOND, 64), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void putItem_ReturnsAllWhenDifferentItem(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 32));
        
        int remaining = inv.putItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        assertEquals(10, remaining);
        assertEquals(ItemStack.of(Material.STONE, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void putItem_ReturnsZeroForEmptyItemStack(VirtualInventory inv, UpdateReason updateReason) {
        int remaining = inv.putItem(updateReason, 0, ItemStack.empty());
        
        assertEquals(0, remaining);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void putItem_RespectsSlotMaxStackSize(VirtualInventory inv, UpdateReason updateReason) {
        inv.setMaxStackSizes(new int[] {16, 64, 64, 64, 64});
        
        int remaining = inv.putItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertEquals(16, remaining);
        assertEquals(ItemStack.of(Material.DIAMOND, 16), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void putItem_ReturnsAllWhenSlotFull(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 64));
        
        int remaining = inv.putItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        assertEquals(10, remaining);
        assertEquals(ItemStack.of(Material.DIAMOND, 64), inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void putItem_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        int remaining = inv.putItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertEquals(32, remaining);
        assertNull(inv.getItem(0));
    }
    
    @Test
    void putItem_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        inv.putItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 32));
        
        assertFalse(eventCalled.get());
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    //</editor-fold>
    
    //<editor-fold desc="setItemAmount">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItemAmount_SetsNewAmount(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        int actualAmount = inv.setItemAmount(updateReason, 0, 32);
        
        assertEquals(32, actualAmount);
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItemAmount_ThrowsWhenSlotEmpty(VirtualInventory inv, UpdateReason updateReason) {
        assertThrows(IllegalStateException.class, () -> inv.setItemAmount(updateReason, 0, 32));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItemAmount_RespectsMaxStackSize(VirtualInventory inv, UpdateReason updateReason) {
        inv.setMaxStackSizes(new int[] {16, 64, 64, 64, 64});
        inv.forceSetItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        int actualAmount = inv.setItemAmount(updateReason, 0, 32);
        
        assertEquals(16, actualAmount);
        assertEquals(ItemStack.of(Material.DIAMOND, 16), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItemAmount_ClearsSlotWhenZero(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        int actualAmount = inv.setItemAmount(updateReason, 0, 0);
        
        assertEquals(0, actualAmount);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void setItemAmount_ClearsSlotWhenNegative(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 32));
        
        int actualAmount = inv.setItemAmount(updateReason, 0, -5);
        
        assertEquals(0, actualAmount);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void setItemAmount_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        int actualAmount = inv.setItemAmount(updateReason, 0, 32);
        
        assertEquals(10, actualAmount);
        assertEquals(ItemStack.of(Material.DIAMOND, 10), inv.getItem(0));
    }
    
    @Test
    void setItemAmount_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        inv.setItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 10));
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        inv.setItemAmount(UpdateReason.SUPPRESSED, 0, 32);
        
        assertFalse(eventCalled.get());
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    //</editor-fold>
    
    //<editor-fold desc="addItemAmount">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItemAmount_AddsToExistingAmount(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        int added = inv.addItemAmount(updateReason, 0, 5);
        
        assertEquals(5, added);
        assertEquals(ItemStack.of(Material.DIAMOND, 15), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItemAmount_ReturnsZeroForEmptySlot(VirtualInventory inv, UpdateReason updateReason) {
        int added = inv.addItemAmount(updateReason, 0, 5);
        
        assertEquals(0, added);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItemAmount_RespectsMaxStackSize(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 60));
        
        int added = inv.addItemAmount(updateReason, 0, 10);
        
        assertEquals(4, added);
        assertEquals(ItemStack.of(Material.DIAMOND, 64), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItemAmount_RemovesWhenNegative(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 5));
        
        int added = inv.addItemAmount(updateReason, 0, -10);
        
        assertEquals(-5, added);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void addItemAmount_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        int added = inv.addItemAmount(updateReason, 0, 5);
        
        assertEquals(0, added);
        assertEquals(ItemStack.of(Material.DIAMOND, 10), inv.getItem(0));
    }
    
    @Test
    void addItemAmount_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        inv.setItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 10));
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        inv.addItemAmount(UpdateReason.SUPPRESSED, 0, 5);
        
        assertFalse(eventCalled.get());
        assertEquals(ItemStack.of(Material.DIAMOND, 15), inv.getItem(0));
    }
    //</editor-fold>
    
    //<editor-fold desc="addItem">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItem_AddsToEmptyInventory(VirtualInventory inv, UpdateReason updateReason) {
        int remaining = inv.addItem(updateReason, ItemStack.of(Material.DIAMOND, 32));
        
        assertEquals(0, remaining);
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItem_AddsToPartialStacksFirst(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 60));
        
        int remaining = inv.addItem(updateReason, ItemStack.of(Material.DIAMOND, 10));
        
        assertEquals(0, remaining);
        assertEquals(ItemStack.of(Material.DIAMOND, 6), inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 64), inv.getItem(1));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItem_FillsMultipleSlots(VirtualInventory inv, UpdateReason updateReason) {
        int remaining = inv.addItem(updateReason, ItemStack.of(Material.DIAMOND, 150));
        
        assertEquals(0, remaining);
        assertEquals(ItemStack.of(Material.DIAMOND, 64), inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 64), inv.getItem(1));
        assertEquals(ItemStack.of(Material.DIAMOND, 22), inv.getItem(2));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItem_ReturnsRemainingWhenInventoryFull(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 64));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 64));
        inv.setItem(updateReason, 2, ItemStack.of(Material.DIAMOND, 64));
        inv.setItem(updateReason, 3, ItemStack.of(Material.DIAMOND, 64));
        inv.setItem(updateReason, 4, ItemStack.of(Material.DIAMOND, 64));
        
        int remaining = inv.addItem(updateReason, ItemStack.of(Material.DIAMOND, 10));
        
        assertEquals(10, remaining);
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItem_SkipsNonSimilarItems(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 32));
        
        int remaining = inv.addItem(updateReason, ItemStack.of(Material.DIAMOND, 10));
        
        assertEquals(0, remaining);
        assertEquals(ItemStack.of(Material.STONE, 32), inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 10), inv.getItem(1));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItem_ReturnsZeroForEmptyItemStack(VirtualInventory inv, UpdateReason updateReason) {
        int remaining = inv.addItem(updateReason, ItemStack.empty());
        
        assertEquals(0, remaining);
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItem_RespectsSlotMaxStackSizes(VirtualInventory inv, UpdateReason updateReason) {
        inv.setMaxStackSizes(new int[] {16, 16, 16, 16, 16});
        
        int remaining = inv.addItem(updateReason, ItemStack.of(Material.DIAMOND, 16 * 6));
        
        assertEquals(16, remaining);
        assertEquals(ItemStack.of(Material.DIAMOND, 16), inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 16), inv.getItem(1));
        assertEquals(ItemStack.of(Material.DIAMOND, 16), inv.getItem(2));
        assertEquals(ItemStack.of(Material.DIAMOND, 16), inv.getItem(3));
        assertEquals(ItemStack.of(Material.DIAMOND, 16), inv.getItem(4));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void addItem_RespectsIterationOrder(VirtualInventory inv, UpdateReason updateReason) {
        inv.setIterationOrder(OperationCategory.ADD, new int[] {2, 1, 0, 3, 4});
        
        inv.addItem(updateReason, ItemStack.of(Material.DIAMOND, 10));
        
        assertNull(inv.getItem(0));
        assertNull(inv.getItem(1));
        assertEquals(ItemStack.of(Material.DIAMOND, 10), inv.getItem(2));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void addItem_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        int remaining = inv.addItem(updateReason, ItemStack.of(Material.DIAMOND, 10));
        
        assertEquals(10, remaining);
        assertNull(inv.getItem(0));
    }
    
    @Test
    void addItem_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        inv.addItem(UpdateReason.SUPPRESSED, ItemStack.of(Material.DIAMOND, 32));
        
        assertFalse(eventCalled.get());
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
    }
    //</editor-fold>
    
    //<editor-fold desc="collectSimilar">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void collectSimilar_CollectsFromPartialStacksFirst(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 64));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 32));
        inv.setItem(updateReason, 2, ItemStack.of(Material.STONE, 30));
        
        int collected = inv.collectSimilar(updateReason, ItemStack.of(Material.DIAMOND), 0);
        
        assertEquals(64, collected);
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(0));
        assertNull(inv.getItem(1));
        assertEquals(ItemStack.of(Material.STONE, 30), inv.getItem(2));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void collectSimilar_RespectsBaseAmount(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 40));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 40));
        
        int collected = inv.collectSimilar(updateReason, ItemStack.of(Material.DIAMOND), 32);
        
        assertEquals(64, collected);
        assertEquals(ItemStack.of(Material.DIAMOND, 8), inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 40), inv.getItem(1));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void collectSimilar_ReturnsBaseAmountWhenAlreadyFull(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        int collected = inv.collectSimilar(updateReason, ItemStack.of(Material.DIAMOND), 64);
        
        assertEquals(64, collected);
        assertEquals(ItemStack.of(Material.DIAMOND, 10), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void collectSimilar_SkipsNonSimilarItems(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 30));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 20));
        
        int collected = inv.collectSimilar(updateReason, ItemStack.of(Material.DIAMOND), 0);
        
        assertEquals(20, collected);
        assertEquals(ItemStack.of(Material.STONE, 30), inv.getItem(0));
        assertNull(inv.getItem(1));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void collectSimilar_RespectsIterationOrder(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 64));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 32));
        inv.setItem(updateReason, 2, ItemStack.of(Material.DIAMOND, 64));
        inv.reverseIterationOrder(OperationCategory.COLLECT);
        
        int collected = inv.collectSimilar(updateReason, ItemStack.of(Material.DIAMOND), 0);
        
        assertEquals(64, collected);
        assertEquals(ItemStack.of(Material.DIAMOND, 64), inv.getItem(0));
        assertNull(inv.getItem(1));
        assertEquals(ItemStack.of(Material.DIAMOND, 32), inv.getItem(2));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void collectSimilar_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 20));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 20));
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        int collected = inv.collectSimilar(updateReason, ItemStack.of(Material.DIAMOND), 0);
        
        // No items collected because event was cancelled
        assertEquals(0, collected);
        assertEquals(ItemStack.of(Material.DIAMOND, 20), inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 20), inv.getItem(1));
    }
    
    @Test
    void collectSimilar_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        inv.setItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 20));
        inv.setItem(UpdateReason.SUPPRESSED, 1, ItemStack.of(Material.DIAMOND, 20));
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        int collected = inv.collectSimilar(UpdateReason.SUPPRESSED, ItemStack.of(Material.DIAMOND), 0);
        
        assertFalse(eventCalled.get());
        assertEquals(40, collected);
        assertNull(inv.getItem(0));
        assertNull(inv.getItem(1));
    }
    //</editor-fold>
    
    //<editor-fold desc="removeIf">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeIf_RemovesMatchingItems(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(updateReason, 1, ItemStack.of(Material.STONE, 20));
        inv.setItem(updateReason, 2, ItemStack.of(Material.DIAMOND, 30));
        
        int removed = inv.removeIf(updateReason, item -> item.getType() == Material.DIAMOND);
        
        assertEquals(40, removed);
        assertNull(inv.getItem(0));
        assertEquals(ItemStack.of(Material.STONE, 20), inv.getItem(1));
        assertNull(inv.getItem(2));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeIf_ReturnsZeroWhenNoMatch(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        
        int removed = inv.removeIf(updateReason, item -> item.getType() == Material.DIAMOND);
        
        assertEquals(0, removed);
        assertEquals(ItemStack.of(Material.STONE, 10), inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeIf_RemovesAllItemsWithTruePredicate(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(updateReason, 1, ItemStack.of(Material.STONE, 20));
        inv.setItem(updateReason, 2, ItemStack.of(Material.GOLD_INGOT, 30));
        
        int removed = inv.removeIf(updateReason, item -> true);
        
        assertEquals(60, removed);
        assertNull(inv.getItem(0));
        assertNull(inv.getItem(1));
        assertNull(inv.getItem(2));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void removeIf_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 20));
        inv.addPreUpdateHandler(event -> {
            if (event.getSlot() == 0) event.setCancelled(true);
        });
        
        int removed = inv.removeIf(updateReason, item -> true);
        
        assertEquals(20, removed);
        assertEquals(ItemStack.of(Material.DIAMOND, 10), inv.getItem(0));
        assertNull(inv.getItem(1));
    }
    
    @Test
    void removeIf_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        inv.setItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(UpdateReason.SUPPRESSED, 1, ItemStack.of(Material.DIAMOND, 20));
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        int removed = inv.removeIf(UpdateReason.SUPPRESSED, item -> true);
        
        assertFalse(eventCalled.get());
        assertEquals(30, removed);
        assertNull(inv.getItem(0));
        assertNull(inv.getItem(1));
    }
    //</editor-fold>
    
    //<editor-fold desc="removeFirst">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeFirst_RemovesUpToAmount(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 20));
        inv.setItem(updateReason, 2, ItemStack.of(Material.DIAMOND, 30));
        
        int removed = inv.removeFirst(updateReason, 25, item -> item.getType() == Material.DIAMOND);
        
        assertEquals(25, removed);
        assertNull(inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 5), inv.getItem(1));
        assertEquals(ItemStack.of(Material.DIAMOND, 30), inv.getItem(2));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeFirst_RemovesLessWhenNotEnough(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        int removed = inv.removeFirst(updateReason, 25, item -> item.getType() == Material.DIAMOND);
        
        assertEquals(10, removed);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeFirst_ReturnsZeroWhenNoMatch(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        
        int removed = inv.removeFirst(updateReason, 25, item -> item.getType() == Material.DIAMOND);
        
        assertEquals(0, removed);
        assertEquals(ItemStack.of(Material.STONE, 10), inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void removeFirst_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 20));
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        int removed = inv.removeFirst(updateReason, 25, item -> true);
        
        assertEquals(0, removed);
        assertEquals(ItemStack.of(Material.DIAMOND, 10), inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 20), inv.getItem(1));
    }
    
    @Test
    void removeFirst_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        inv.setItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(UpdateReason.SUPPRESSED, 1, ItemStack.of(Material.DIAMOND, 20));
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        int removed = inv.removeFirst(UpdateReason.SUPPRESSED, 25, item -> true);
        
        assertFalse(eventCalled.get());
        assertEquals(25, removed);
        assertNull(inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 5), inv.getItem(1));
    }
    //</editor-fold>
    
    //<editor-fold desc="removeSimilar">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeSimilar_RemovesSimilarItems(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(updateReason, 1, ItemStack.of(Material.STONE, 20));
        inv.setItem(updateReason, 2, ItemStack.of(Material.DIAMOND, 30));
        
        int removed = inv.removeSimilar(updateReason, ItemStack.of(Material.DIAMOND));
        
        assertEquals(40, removed);
        assertNull(inv.getItem(0));
        assertEquals(ItemStack.of(Material.STONE, 20), inv.getItem(1));
        assertNull(inv.getItem(2));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeSimilar_ReturnsZeroWhenNoMatch(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        
        int removed = inv.removeSimilar(updateReason, ItemStack.of(Material.DIAMOND));
        
        assertEquals(0, removed);
        assertEquals(ItemStack.of(Material.STONE, 10), inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void removeSimilar_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 20));
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        int removed = inv.removeSimilar(updateReason, ItemStack.of(Material.DIAMOND));
        
        assertEquals(0, removed);
        assertEquals(ItemStack.of(Material.DIAMOND, 10), inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 20), inv.getItem(1));
    }
    
    @Test
    void removeSimilar_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        inv.setItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(UpdateReason.SUPPRESSED, 1, ItemStack.of(Material.DIAMOND, 20));
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        int removed = inv.removeSimilar(UpdateReason.SUPPRESSED, ItemStack.of(Material.DIAMOND));
        
        assertFalse(eventCalled.get());
        assertEquals(30, removed);
        assertNull(inv.getItem(0));
        assertNull(inv.getItem(1));
    }
    //</editor-fold>
    
    //<editor-fold desc="removeFirstSimilar">
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeFirstSimilar_RemovesUpToAmount(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 20));
        
        int removed = inv.removeFirstSimilar(updateReason, 15, ItemStack.of(Material.DIAMOND));
        
        assertEquals(15, removed);
        assertNull(inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 15), inv.getItem(1));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeFirstSimilar_RemovesLessWhenNotEnough(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        
        int removed = inv.removeFirstSimilar(updateReason, 25, ItemStack.of(Material.DIAMOND));
        
        assertEquals(10, removed);
        assertNull(inv.getItem(0));
    }
    
    @MethodSource("updateReasons")
    @ParameterizedTest
    void removeFirstSimilar_ReturnsZeroWhenNoMatch(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.STONE, 10));
        
        int removed = inv.removeFirstSimilar(updateReason, 25, ItemStack.of(Material.DIAMOND));
        
        assertEquals(0, removed);
        assertEquals(ItemStack.of(Material.STONE, 10), inv.getItem(0));
    }
    
    @MethodSource("unsuppressedUpdateReasons")
    @ParameterizedTest
    void removeFirstSimilar_EventCancelled(VirtualInventory inv, UpdateReason updateReason) {
        inv.setItem(updateReason, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(updateReason, 1, ItemStack.of(Material.DIAMOND, 20));
        inv.addPreUpdateHandler(event -> event.setCancelled(true));
        
        int removed = inv.removeFirstSimilar(updateReason, 25, ItemStack.of(Material.DIAMOND));
        
        assertEquals(0, removed);
        assertEquals(ItemStack.of(Material.DIAMOND, 10), inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 20), inv.getItem(1));
    }
    
    @Test
    void removeFirstSimilar_SuppressedUpdateReasonSkipsEvents() {
        var inv = new VirtualInventory(5);
        inv.setItem(UpdateReason.SUPPRESSED, 0, ItemStack.of(Material.DIAMOND, 10));
        inv.setItem(UpdateReason.SUPPRESSED, 1, ItemStack.of(Material.DIAMOND, 20));
        AtomicBoolean eventCalled = new AtomicBoolean(false);
        inv.addPreUpdateHandler(event -> eventCalled.set(true));
        
        int removed = inv.removeFirstSimilar(UpdateReason.SUPPRESSED, 25, ItemStack.of(Material.DIAMOND));
        
        assertFalse(eventCalled.get());
        assertEquals(25, removed);
        assertNull(inv.getItem(0));
        assertEquals(ItemStack.of(Material.DIAMOND, 5), inv.getItem(1));
    }
    //</editor-fold>
    
    //<editor-fold desc="simulateSingleAdd">
    @Test
    void simulateSingleAdd_ReturnsZeroWhenFits() {
        var inv = new VirtualInventory(5);
        int remaining = inv.simulateSingleAdd(ItemStack.of(Material.DIAMOND, 32));
        
        assertEquals(0, remaining);
        assertNull(inv.getItem(0)); // Inventory unchanged
    }
    
    @Test
    void simulateSingleAdd_ReturnsRemainingWhenDoesNotFit() {
        var inv = new VirtualInventory(1);
        inv.setItem(null, 0, ItemStack.of(Material.DIAMOND, 60));
        
        int remaining = inv.simulateSingleAdd(ItemStack.of(Material.DIAMOND, 10));
        
        assertEquals(6, remaining);
        assertEquals(ItemStack.of(Material.DIAMOND, 60), inv.getItem(0)); // Inventory unchanged
    }
    //</editor-fold>
    
    //<editor-fold desc="simulateAdd">
    @Test
    void simulateAdd_ReturnsArrayForMultipleItems() {
        var inv = new VirtualInventory(2);
        inv.setItem(null, 0, ItemStack.of(Material.COBWEB));
        
        int[] remaining = inv.simulateAdd(ItemStack.of(Material.DIAMOND, 32), ItemStack.of(Material.DIAMOND, 50));
        
        assertArrayEquals(new int[] {0, 18}, remaining);
        
        // assert inventory unchanged
        assertEquals(ItemStack.of(Material.COBWEB), inv.getItem(0));
        assertNull(inv.getItem(1));
    }
    
    @Test
    void simulateAdd_ReturnsZeroForEmptyItem() {
        var inv = new VirtualInventory(1);
        int remaining = inv.simulateSingleAdd(ItemStack.empty());
        
        assertEquals(0, remaining);
    }
    //</editor-fold>
    
    //<editor-fold desc="canHold">
    @Test
    void canHold_ReturnsTrueWhenFits() {
        var inv = new VirtualInventory(1);
        boolean canHold = inv.canHold(ItemStack.of(Material.DIAMOND, 32));
        
        assertTrue(canHold);
    }
    
    @Test
    void canHold_ReturnsFalseWhenDoesNotFit() {
        var inv = new VirtualInventory(1);
        inv.setItem(null, 0, ItemStack.of(Material.DIAMOND, 60));
        
        boolean canHold = inv.canHold(ItemStack.of(Material.DIAMOND, 10));
        
        assertFalse(canHold);
    }
    
    @Test
    void canHold_ChecksMultipleItems() {
        var inv = new VirtualInventory(2);
        boolean canHold = inv.canHold(ItemStack.of(Material.DIAMOND, 64), ItemStack.of(Material.DIAMOND, 64));
        
        assertTrue(canHold);
    }
    
    @Test
    void canHold_ReturnsFalseWhenMultipleItemsDontFit() {
        var inv = new VirtualInventory(1);
        boolean canHold = inv.canHold(ItemStack.of(Material.DIAMOND, 64), ItemStack.of(Material.DIAMOND, 65));
        
        assertFalse(canHold);
    }
    //</editor-fold>
    
}

