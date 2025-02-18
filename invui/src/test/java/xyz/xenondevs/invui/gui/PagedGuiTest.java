package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class PagedGuiTest {
    
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
    public void testPageContentUpdateOnVirtualInventoryResize() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        
        var gui = PagedGui.inventories()
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
    public void testPagedInventoriesRemoveResizeHandlers() {
        var inv1 = new VirtualInventory(3);
        var inv2 = new VirtualInventory(3);
        
        var gui = PagedGui.inventories()
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
    
    @Test
    public void testPageContentChangeWithApplyStructure() {
        var player = server.addPlayer();
        
        var preset = IngredientPreset.builder()
            .addIngredient('#', Item.simple(ItemStack.of(Material.BLACK_STAINED_GLASS)))
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .build();
        var s1 = new Structure(
            "x x x",
            "x x x",
            "# # #"
        ).applyPreset(preset);
        var s2 = new Structure(
            ". . .",
            "x x x",
            "# # #"
        ).applyPreset(preset);
        var content = IntStream.range(0, 100)
            .mapToObj(i -> Item.simple(ItemStack.of(Material.DIAMOND)))
            .toList();
        var gui = PagedGui.items()
            .setStructure(s1)
            .setContent(content)
            .build();
        
        // validate assumptions for s1
        for (int i = 0; i < 6; i++) {
            var element = gui.getSlotElement(i);
            assertNotNull(element);
            
            var holdingElement = element.getHoldingElement();
            assertInstanceOf(SlotElement.Item.class, holdingElement);
            
            var item = ((SlotElement.Item) holdingElement).item();
            assertSame(content.get(i), item);
        }
        for (int i = 6; i < 9; i++) {
            var element = gui.getSlotElement(i);
            assertNotNull(element);
            
            var itemStack = element.getItemStack(player);
            assertNotNull(itemStack);
            assertEquals(Material.BLACK_STAINED_GLASS, itemStack.getType());
        }
        
        gui.applyStructure(s2);
        
        // validate assumptions for s2
        for (int i = 0; i < 3; i++) {
            // element or holding element should be null
            var element = gui.getSlotElement(i);
            if (element != null) {
                assertNull(element.getHoldingElement());
            }
        }
        for (int i = 3; i < 6; i++) {
            var element = gui.getSlotElement(i);
            assertNotNull(element);
            
            var holdingElement = element.getHoldingElement();
            assertInstanceOf(SlotElement.Item.class, holdingElement);
            
            var item = ((SlotElement.Item) holdingElement).item();
            assertSame(content.get(i - 3), item);
        }
        for (int i = 6; i < 9; i++) {
            var element = gui.getSlotElement(i);
            assertNotNull(element);
            
            var itemStack = element.getItemStack(player);
            assertNotNull(itemStack);
            assertEquals(Material.BLACK_STAINED_GLASS, itemStack.getType());
        }
    }
    
}
