package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompositeInventoryTest {
    
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
    public void testIterationOrderInComposedInventories() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        inv2.reverseIterationOrder();
        var inv3 = new VirtualInventory(3);
        inv3.setIterationOrder(OperationCategory.ADD, new int[] {2, 0, 1});
        
        var composite = new CompositeInventory(inv1, inv2, inv3);
        assertArrayEquals(new int[] {0, 1, 2, 5, 4, 3, 8, 6, 7}, composite.getIterationOrder(OperationCategory.ADD));
        assertArrayEquals(new int[] {0, 1, 2, 5, 4, 3, 6, 7, 8}, composite.getIterationOrder(OperationCategory.COLLECT));
    }
    
    @Test
    public void testIterationOrderInCompositeInventory() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        var inv3 = new VirtualInventory(3);
        
        var composite = new CompositeInventory(inv1, inv2, inv3);
        composite.reverseIterationOrder(OperationCategory.ADD);
        assertArrayEquals(new int[] {8, 7, 6, 5, 4, 3, 2, 1, 0}, composite.getIterationOrder(OperationCategory.ADD));
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, composite.getIterationOrder(OperationCategory.COLLECT));
        
        composite.addItem(null, ItemStack.of(Material.DIAMOND));
        assertEquals(ItemStack.of(Material.DIAMOND), inv3.getItem(2));
    }
    
    @Test
    public void testIterationOrderInComposedAndCompositeInventory() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        inv2.reverseIterationOrder();
        var inv3 = new VirtualInventory(3);
        inv3.setIterationOrder(OperationCategory.ADD, new int[] {2, 0, 1});
        
        var composite = new CompositeInventory(inv1, inv2, inv3);
        composite.reverseIterationOrder(OperationCategory.ADD);
        assertArrayEquals(new int[] {7, 6, 8, 3, 4, 5, 2, 1, 0}, composite.getIterationOrder(OperationCategory.ADD));
        assertArrayEquals(new int[] {0, 1, 2, 5, 4, 3, 6, 7, 8}, composite.getIterationOrder(OperationCategory.COLLECT));
    }
    
    @Test
    public void testMaxStackSizesTakenFromComposedInventories() {
        var inv1 = new VirtualInventory(3);
        inv1.setMaxStackSizes(new int[] {99, 16, 64});
        var inv2 = new VirtualInventory(3);
        inv2.setMaxStackSizes(new int[] {1, 32, 64});
        var composite = new CompositeInventory(inv1, inv2);
        
        assertArrayEquals(new int[] {99, 16, 64, 1, 32, 64}, composite.getMaxStackSizes());
    }
    
    @Test
    public void testClickHandlersInComposedAndCompositeInventory() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        var composite = new CompositeInventory(inv1, inv2);
        
        AtomicInteger state = new AtomicInteger();
        AtomicInteger inv1ClickedAt = new AtomicInteger();
        AtomicInteger inv2ClickedAt = new AtomicInteger();
        AtomicInteger compositeClickedAt = new AtomicInteger();
        
        inv1.addClickHandler(event -> inv1ClickedAt.set(state.incrementAndGet()));
        inv2.addClickHandler(event -> inv2ClickedAt.set(state.incrementAndGet()));
        composite.addClickHandler(event -> compositeClickedAt.set(state.incrementAndGet()));
        
        var player = server.addPlayer();
        
        composite.callClickEvent(0, new Click(player, ClickType.LEFT), InventoryAction.UNKNOWN);
        
        assertEquals(1, inv1ClickedAt.get());
        assertEquals(0, inv2ClickedAt.get());
        assertEquals(2, compositeClickedAt.get());
        
        composite.callClickEvent(5, new Click(player, ClickType.LEFT), InventoryAction.UNKNOWN);
        
        assertEquals(1, inv1ClickedAt.get());
        assertEquals(3, inv2ClickedAt.get());
        assertEquals(4, compositeClickedAt.get());
        
        inv1.callClickEvent(0, new Click(player, ClickType.LEFT), InventoryAction.UNKNOWN);
        inv2.callClickEvent(0, new Click(player, ClickType.LEFT), InventoryAction.UNKNOWN);
        
        assertEquals(5, inv1ClickedAt.get());
        assertEquals(6, inv2ClickedAt.get());
        assertEquals(4, compositeClickedAt.get());
    }
    
    @Test
    public void testClickEventStateVisibleInCompositeHandler() {
        var inv1 = new VirtualInventory(3);
        var composite = new CompositeInventory(inv1);
        
        AtomicBoolean doCancel = new AtomicBoolean();
        inv1.addClickHandler(event -> event.setCancelled(doCancel.get()));
        composite.addClickHandler(event -> assertEquals(event.isCancelled(), doCancel.get()));
        
        var player = server.addPlayer();
        
        doCancel.set(false);
        composite.callClickEvent(0, new Click(player, ClickType.LEFT), InventoryAction.UNKNOWN);
        
        doCancel.set(true);
        composite.callClickEvent(0, new Click(player, ClickType.LEFT), InventoryAction.UNKNOWN);
    }
    
    @Test
    public void testPreUpdateHandlersInComposedAndCompositeInventory() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        var composite = new CompositeInventory(inv1, inv2);
        
        AtomicInteger state = new AtomicInteger();
        AtomicInteger inv1UpdatedAt = new AtomicInteger();
        AtomicInteger inv2UpdatedAt = new AtomicInteger();
        AtomicInteger compositeUpdatedAt = new AtomicInteger();
        
        inv1.addPreUpdateHandler(event -> inv1UpdatedAt.set(state.incrementAndGet()));
        inv2.addPreUpdateHandler(event -> inv2UpdatedAt.set(state.incrementAndGet()));
        composite.addPreUpdateHandler(event -> compositeUpdatedAt.set(state.incrementAndGet()));
        
        composite.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        assertEquals(1, inv1UpdatedAt.get());
        assertEquals(0, inv2UpdatedAt.get());
        assertEquals(2, compositeUpdatedAt.get());
        
        composite.setItem(null, 5, ItemStack.of(Material.DIAMOND));
        
        assertEquals(1, inv1UpdatedAt.get());
        assertEquals(3, inv2UpdatedAt.get());
        assertEquals(4, compositeUpdatedAt.get());
        
        inv1.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        inv2.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        assertEquals(5, inv1UpdatedAt.get());
        assertEquals(6, inv2UpdatedAt.get());
        assertEquals(4, compositeUpdatedAt.get());
    }
    
    @Test
    public void testPreUpdateEventStateVisibleInCompositeHandler() {
        var inv1 = new VirtualInventory(3);
        var composite = new CompositeInventory(inv1);
        
        AtomicBoolean doCancel = new AtomicBoolean();
        inv1.addPreUpdateHandler(event -> {
            event.setCancelled(doCancel.get());
        });
        composite.addPreUpdateHandler(event -> {
            assertEquals(event.isCancelled(), doCancel.get());
        });
        
        doCancel.set(false);
        composite.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        doCancel.set(true);
        composite.setItem(null, 0, ItemStack.of(Material.DIAMOND));
    }
    
    @Test
    public void testPostUpdateHandlersInComposedAndCompositeInventory() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        var composite = new CompositeInventory(inv1, inv2);
        
        AtomicInteger state = new AtomicInteger();
        AtomicInteger inv1UpdatedAt = new AtomicInteger();
        AtomicInteger inv2UpdatedAt = new AtomicInteger();
        AtomicInteger compositeUpdatedAt = new AtomicInteger();
        
        inv1.addPostUpdateHandler(event -> inv1UpdatedAt.set(state.incrementAndGet()));
        inv2.addPostUpdateHandler(event -> inv2UpdatedAt.set(state.incrementAndGet()));
        composite.addPostUpdateHandler(event -> compositeUpdatedAt.set(state.incrementAndGet()));
        
        composite.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        assertEquals(1, inv1UpdatedAt.get());
        assertEquals(0, inv2UpdatedAt.get());
        assertEquals(2, compositeUpdatedAt.get());
        
        composite.setItem(null, 5, ItemStack.of(Material.DIAMOND));
        
        assertEquals(1, inv1UpdatedAt.get());
        assertEquals(3, inv2UpdatedAt.get());
        assertEquals(4, compositeUpdatedAt.get());
        
        inv1.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        inv2.setItem(null, 0, ItemStack.of(Material.DIAMOND));
        
        assertEquals(5, inv1UpdatedAt.get());
        assertEquals(6, inv2UpdatedAt.get());
        assertEquals(4, compositeUpdatedAt.get());
    }
    
}
