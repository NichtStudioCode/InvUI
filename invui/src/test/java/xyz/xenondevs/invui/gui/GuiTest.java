package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.Item;

import static org.junit.jupiter.api.Assertions.*;

public class GuiTest {
    
    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
    }
    
    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    public void testGuiBuilderGuiIngredient() {
        var inner = Gui.empty(3, 3);
        inner.fill(Item.simple(new ItemStack(Material.DIAMOND)), true);
        
        var outer = Gui.normal()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inner)
            .build();
        
        assertSequentialGuiLink(outer, inner);
    }
    
    @Test
    public void testGuiBuilderGuiIngredientNotEnoughSlots() {
        var inner = Gui.empty(3, 3);
        inner.fill(Item.simple(new ItemStack(Material.DIAMOND)), true);
        
        var outer = Gui.normal()
            .setStructure(
                ". . .",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inner)
            .build();
        
        for (int i = 3; i < 9; i++) {
            var element = outer.getSlotElement(i);
            assertInstanceOf(SlotElement.GuiLink.class, element);
            
            var guiLink = (SlotElement.GuiLink) element;
            assertSame(inner, guiLink.gui());
            assertEquals(i - 3, guiLink.slot());
        }
    }
    
    @Test
    public void testGuiBuilderGuiIngredientTooManySlots() {
        var inner = Gui.empty(3, 2);
        inner.fill(Item.simple(new ItemStack(Material.DIAMOND)), true);
        
        var outer = Gui.normal()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inner)
            .build();
        
        assertSequentialGuiLink(outer, inner);
    }
    
    @Test
    public void testGuiBuilderGuiIngredientInvokedTwice() {
        var inner = Gui.empty(3, 3);
        inner.fill(Item.simple(new ItemStack(Material.DIAMOND)), true);
        
        var builder = Gui.normal()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inner);
        
        assertSequentialGuiLink(builder.build(), inner);
        builder.addIngredient('a', Markers.CONTENT_LIST_SLOT_HORIZONTAL); // modify builder to invalidate cache
        assertSequentialGuiLink(builder.build(), inner);
    }
    
    private void assertSequentialGuiLink(Gui gui, Gui linked) {
        for (int i = 0; i < gui.getSize(); i++) {
            var element = gui.getSlotElement(i);
            assertInstanceOf(SlotElement.GuiLink.class, element);
            
            var guiLink = (SlotElement.GuiLink) element;
            assertSame(linked, guiLink.gui());
            assertEquals(i % linked.getSize(), guiLink.slot());
        }
    }
    
    @Test
    public void testGuiBuilderInventoryIngredient() {
        var inv = new VirtualInventory(9);
        
        var outer = Gui.normal()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inv)
            .build();
        
        assertSequentialInventoryLink(outer, inv);
    }
    
    @Test
    public void testGuiBuilderInventoryIngredientNotEnoughSlots() {
        var inv = new VirtualInventory(9);
        
        var outer = Gui.normal()
            .setStructure(
                ". . .",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inv)
            .build();
        
        for (int i = 3; i < 9; i++) {
            var element = outer.getSlotElement(i);
            assertInstanceOf(SlotElement.InventoryLink.class, element);
            
            var guiLink = (SlotElement.InventoryLink) element;
            assertSame(inv, guiLink.inventory());
            assertEquals(i - 3, guiLink.slot());
        }
    }
    
    @Test
    public void testGuiBuilderInventoryIngredientTooManySlots() {
        var inv = new VirtualInventory(6);
        
        var builder = Gui.normal()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inv);
        
        assertSequentialInventoryLink(builder.build(), inv);
    }
    
    @Test
    public void testGuiBuilderInventoryIngredientInvokedTwice() {
        var inv = new VirtualInventory(9);
        
        var builder = Gui.normal()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inv);
        
        assertSequentialInventoryLink(builder.build(), inv);
        builder.addIngredient('a', Markers.CONTENT_LIST_SLOT_HORIZONTAL); // modify builder to invalidate cache
        assertSequentialInventoryLink(builder.build(), inv);
    }
    
    private void assertSequentialInventoryLink(Gui gui, Inventory linked) {
        for (int i = 0; i < gui.getSize(); i++) {
            var element = gui.getSlotElement(i);
            assertInstanceOf(SlotElement.InventoryLink.class, element);
            
            var inventoryLink = (SlotElement.InventoryLink) element;
            assertSame(linked, inventoryLink.inventory());
            assertEquals(i % linked.getSize(), inventoryLink.slot());
        }
    }
}
