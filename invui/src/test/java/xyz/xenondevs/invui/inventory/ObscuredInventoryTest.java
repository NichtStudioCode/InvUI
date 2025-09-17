package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.InvUI;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObscuredInventoryTest {
    
    private static ServerMock server;
    
    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        InvUI.getInstance().setUncaughtExceptionHandler((msg, t) -> {
            throw new AssertionError(msg, t);
        });
    }
    
    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    public void testIterationOrderInBackingInventory() {
        var inv = new VirtualInventory(6);
        inv.setIterationOrder(OperationCategory.ADD, new int[] {4, 3, 0, 1, 2, 5});
        
        var obscured = new ObscuredInventory(inv, i -> i % 2 == 0);
        assertArrayEquals(new int[] {1, 0, 2}, obscured.getIterationOrder(OperationCategory.ADD));
        assertArrayEquals(new int[] {0, 1, 2}, obscured.getIterationOrder(OperationCategory.COLLECT));
    }
    
    @Test
    public void testIterationOrderInObscuredInventory() {
        var inv = new VirtualInventory(6);
        var obscured = new ObscuredInventory(inv, i -> i % 2 == 0);
        obscured.reverseIterationOrder(OperationCategory.ADD);
        
        assertArrayEquals(new int[] {2, 1, 0}, obscured.getIterationOrder(OperationCategory.ADD));
        assertArrayEquals(new int[] {0, 1, 2}, obscured.getIterationOrder(OperationCategory.COLLECT));
    }
    
    @Test
    public void testIterationOrderInBackingAndObscuredInventory() {
        var inv = new VirtualInventory(6);
        inv.setIterationOrder(new int[] {4, 3, 0, 1, 2, 5});
        var obscured = new ObscuredInventory(inv, i -> i % 2 == 0);
        obscured.reverseIterationOrder(OperationCategory.ADD);
        
        assertArrayEquals(new int[] {2, 0, 1}, obscured.getIterationOrder(OperationCategory.ADD));
        assertArrayEquals(new int[] {1, 0, 2}, obscured.getIterationOrder(OperationCategory.COLLECT));
    }
    
    @Test
    public void testGuiPriorityOverriding() {
        var inv = new VirtualInventory(6);
        inv.setGuiPriority(5);
        var obscured = new ObscuredInventory(inv, i -> i % 2 == 0);
        
        // by default, ObscuredInventory uses the priority of the backing inventory
        assertEquals(5, obscured.getGuiPriority(OperationCategory.ADD));
        assertEquals(5, obscured.getGuiPriority(OperationCategory.COLLECT));
        
        inv.setGuiPriority(OperationCategory.ADD, 10);
        assertEquals(10, obscured.getGuiPriority(OperationCategory.ADD));
        assertEquals(5, obscured.getGuiPriority(OperationCategory.COLLECT));
        
        // only when explicitly set on obscured inventory is the gui priority overridden
        obscured.setGuiPriority(OperationCategory.ADD, 0);
        assertEquals(0, obscured.getGuiPriority(OperationCategory.ADD));
        assertEquals(5, obscured.getGuiPriority(OperationCategory.COLLECT));
        
        // once the gui priority is overridden, changes to the backing inventory do not affect it anymore
        inv.setGuiPriority(OperationCategory.ADD, 20);
        assertEquals(0, obscured.getGuiPriority(OperationCategory.ADD));
        assertEquals(5, obscured.getGuiPriority(OperationCategory.COLLECT));
    }
    
    @Test
    public void testMaxStackSizesTakenFromBackingInventory() {
        var backing = new VirtualInventory(3);
        backing.setMaxStackSizes(new int[] {99, 16, 64});
        var obscured = new ObscuredInventory(backing, i -> i == 0);
        
        assertArrayEquals(new int[] {16, 64}, obscured.getMaxStackSizes());
    }
    
    @Test
    public void testClickHandlersInBackingAndObscuredInventory() {
        var backing = new VirtualInventory(3);
        var obscured = new ObscuredInventory(backing, i -> i == 0);
        
        AtomicInteger state = new AtomicInteger();
        AtomicInteger backingClickedAt = new AtomicInteger();
        AtomicInteger obscuredClickedAt = new AtomicInteger();
        
        backing.addClickHandler(event -> backingClickedAt.set(state.incrementAndGet()));
        obscured.addClickHandler(event -> obscuredClickedAt.set(state.incrementAndGet()));
        
        var player = server.addPlayer();
        
        obscured.callClickEvent(0, new Click(player, ClickType.LEFT));
        
        assertEquals(1, backingClickedAt.get());
        assertEquals(2, obscuredClickedAt.get());
        
        backing.callClickEvent(0, new Click(player, ClickType.LEFT));
        
        assertEquals(3, backingClickedAt.get());
        assertEquals(2, obscuredClickedAt.get());
    }
    
    @Test
    public void testClickEventStateVisibleInObscuredHandler() {
        var backing = new VirtualInventory(3);
        var obscured = new ObscuredInventory(backing, i -> i == 0);
        
        AtomicBoolean doCancel = new AtomicBoolean();
        backing.addClickHandler(event -> event.setCancelled(doCancel.get()));
        obscured.addClickHandler(event -> assertEquals(event.isCancelled(), doCancel.get()));
        
        var player = server.addPlayer();
        
        doCancel.set(false);
        obscured.callClickEvent(0, new Click(player, ClickType.LEFT));
        
        doCancel.set(true);
        obscured.callClickEvent(0, new Click(player, ClickType.LEFT));
    }
    
    @Test
    public void testPreUpdateHandlersInBackingAndObscuredInventory() {
        var backing = new VirtualInventory(3);
        var obscured = new ObscuredInventory(backing, i -> i == 0);
        
        AtomicInteger state = new AtomicInteger();
        AtomicInteger backingClickedAt = new AtomicInteger();
        AtomicInteger obscuredClickedAt = new AtomicInteger();
        
        backing.addPreUpdateHandler(event -> backingClickedAt.set(state.incrementAndGet()));
        obscured.addPreUpdateHandler(event -> obscuredClickedAt.set(state.incrementAndGet()));
        
        obscured.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        assertEquals(1, backingClickedAt.get());
        assertEquals(2, obscuredClickedAt.get());
        
        backing.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        assertEquals(3, backingClickedAt.get());
        assertEquals(2, obscuredClickedAt.get());
    }
    
    @Test
    public void testPreUpdateEventStateVisibleInObscuredHandler() {
        var backing = new VirtualInventory(3);
        var obscured = new ObscuredInventory(backing, i -> i == 0);
        
        AtomicBoolean doCancel = new AtomicBoolean();
        AtomicReference<ItemStack> newItemStack = new AtomicReference<>(ItemStack.empty());
        backing.addPreUpdateHandler(event -> {
            event.setCancelled(doCancel.get());
            event.setNewItem(newItemStack.get());
        });
        obscured.addPreUpdateHandler(event -> {
            assertEquals(event.isCancelled(), doCancel.get());
            assertEquals(event.getNewItem(), newItemStack.get());
        });
        
        doCancel.set(false);
        newItemStack.set(ItemStack.of(Material.STONE));
        obscured.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        doCancel.set(true);
        newItemStack.set(ItemStack.of(Material.STONE));
        obscured.setItem(null, 0, ItemStack.of(Material.DIAMOND));
    }
    
    @Test
    public void testPostUpdateHandlersInBackingAndObscuredInventory() {
        var backing = new VirtualInventory(3);
        var obscured = new ObscuredInventory(backing, i -> i == 0);
        
        AtomicInteger state = new AtomicInteger();
        AtomicInteger backingClickedAt = new AtomicInteger();
        AtomicInteger obscuredClickedAt = new AtomicInteger();
        
        backing.addPostUpdateHandler(event -> backingClickedAt.set(state.incrementAndGet()));
        obscured.addPostUpdateHandler(event -> obscuredClickedAt.set(state.incrementAndGet()));
        
        obscured.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        assertEquals(1, backingClickedAt.get());
        assertEquals(2, obscuredClickedAt.get());
        
        backing.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        assertEquals(3, backingClickedAt.get());
        assertEquals(2, obscuredClickedAt.get());
    }
    
}
