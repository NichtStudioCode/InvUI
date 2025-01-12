package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.plugin.PluginMock;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.Item;

import static org.junit.jupiter.api.Assertions.*;

public class GuiTest {
    
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
        
        for (int i = 0; i < 9; i++) {
            var element = outer.getSlotElement(i);
            assertInstanceOf(SlotElement.GuiLink.class, element);
            
            var guiLink = (SlotElement.GuiLink) element;
            assertSame(inner, guiLink.gui());
            assertEquals(i, guiLink.slot());
        }
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
        
        var builder = Gui.normal()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inner);
        
        assertThrows(Exception.class, builder::build);
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
        
        for (int i = 0; i < 9; i++) {
            var element = outer.getSlotElement(i);
            assertInstanceOf(SlotElement.InventoryLink.class, element);
            
            var guiLink = (SlotElement.InventoryLink) element;
            assertSame(inv, guiLink.inventory());
            assertEquals(i, guiLink.slot());
        }
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
        
        assertThrows(Exception.class, builder::build);
    }
}
