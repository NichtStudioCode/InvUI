package xyz.xenondevs.invui.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class ScrollInventoriesGuiTest {
    
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static ServerMock server;
    
    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
    }
    
    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    public void testScrollInventoriesInitWithBuilder() {
        testScrollInventoriesInit(content ->
            ScrollGui.inventories()
                .setStructure(
                    "x x x",
                    "x x x",
                    "x x x"
                )
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .setContent(content)
                .build()
        );
    }
    
    @Test
    public void testScrollInventoriesInitWithStaticFactoryFunction() {
        testScrollInventoriesInit(content ->
            ScrollGui.ofInventories(
                3, 3,
                content,
                SlotUtils.toSlotSet(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, 3),
                ScrollGui.ScrollDirection.VERTICAL
            )
        );
    }
    
    private void testScrollInventoriesInit(Function<? super List<Inventory>, ? extends Gui> createGui) {
        var content = List.<Inventory>of(new VirtualInventory(3), new VirtualInventory(3));
        
        var gui = createGui.apply(content);
        
        for (int i = 0; i < 6; i++) {
            var element = gui.getSlotElement(i);
            assertInstanceOf(SlotElement.InventoryLink.class, element, "i=" + i);
            assertSame(content.get(i / 3), ((SlotElement.InventoryLink) element).inventory(), "i= " + i);
            assertEquals(i % 3, ((SlotElement.InventoryLink) element).slot(), "i= " + i);
        }
        for (int i = 6; i < 9; i++) {
            assertNull(gui.getSlotElement(i), "i=" + i);
        }
    }
    
    @Test
    public void testScrollContentUpdateOnVirtualInventoryResize() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        
        var gui = ScrollGui.inventories()
            .setStructure(
                "x x x",
                "x x x",
                "x x x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(List.of(inv1, inv2))
            .build();
        
        for (int i = 0; i < 6; i++) {
            assertInstanceOf(SlotElement.InventoryLink.class, gui.getSlotElement(i));
        }
        for (int i = 6; i < 9; i++) {
            assertNull(gui.getSlotElement(i));
        }
        
        // resize to fill entire gui with inventories
        inv1.resize(5);
        inv2.resize(4);
        
        for (int i = 0; i < 9; i++) {
            assertInstanceOf(SlotElement.InventoryLink.class, gui.getSlotElement(i));
        }
        
        // resize to original state
        inv1.resize(3);
        inv2.resize(3);
        
        for (int i = 0; i < 6; i++) {
            assertInstanceOf(SlotElement.InventoryLink.class, gui.getSlotElement(i));
        }
        for (int i = 6; i < 9; i++) {
            assertNull(gui.getSlotElement(i));
        }
    }
    
    @Test
    public void testScrollInventoriesRemoveResizeHandlers() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        
        var gui = ScrollGui.inventories()
            .setStructure(
                "x x x",
                "x x x",
                "x x x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(List.of(inv1))
            .build();
        
        assertEquals(1, Objects.requireNonNull(inv1.getResizeHandlers()).size());
        assertTrue(inv2.getResizeHandlers() == null || inv2.getResizeHandlers().isEmpty());
        
        gui.setContent(List.of(inv2));
        
        assertTrue(inv1.getResizeHandlers() == null || inv1.getResizeHandlers().isEmpty());
        assertEquals(1, Objects.requireNonNull(inv2.getResizeHandlers()).size());
    }
    
}
