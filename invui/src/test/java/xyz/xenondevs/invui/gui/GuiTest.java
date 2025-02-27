package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemWrapper;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class GuiTest {
    
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
    
    @Test
    public void testGetSlots() {
        var gui = Gui.normal()
            .setStructure(
                "a b c",
                "a a a",
                "b b c"
            ).build();
        
        assertEquals(List.of(new Slot(0, 0), new Slot(0, 1), new Slot(1, 1), new Slot(2, 1)), gui.getSlots('a'));
        assertEquals(List.of(new Slot(1, 0), new Slot(0, 2), new Slot(1, 2)), gui.getSlots('b'));
        assertEquals(List.of(new Slot(2, 0), new Slot(2, 2)), gui.getSlots('c'));
    }
    
    @Test
    public void testIsTagged() {
        var gui = Gui.normal()
            .setStructure(
                "a b c",
                "a a a",
                "b b c"
            ).build();
        
        assertTrue(gui.isTagged(new Slot(0, 0), 'a'));
        assertTrue(gui.isTagged(new Slot(1, 2), 'b'));
        assertTrue(gui.isTagged(new Slot(2, 2), 'c'));
        assertFalse(gui.isTagged(new Slot(1, 0), 'c'));
        assertFalse(gui.isTagged(new Slot(2, 2), 'x'));
    }
    
    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testSetItemUsingIngredientKey() {
        var gui = Gui.normal()
            .setStructure("a a b")
            .addIngredient('a', Item.simple(ItemStack.of(Material.DIRT)))
            .addIngredient('b', Item.simple(ItemStack.of(Material.DIAMOND)))
            .build();
        var player = server.addPlayer();
        
        gui.setItem('a', Item.simple(ItemStack.of(Material.DIAMOND_BLOCK)));
        
        assertEquals(Material.DIAMOND_BLOCK, gui.getSlotElement(0).getItemStack(player).getType());
        assertEquals(Material.DIAMOND_BLOCK, gui.getSlotElement(1).getItemStack(player).getType());
        assertEquals(Material.DIAMOND, gui.getSlotElement(2).getItemStack(player).getType());
    }
    
    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testSetItemBuilderUsingIngredientKey() {
        var gui = Gui.normal()
            .setStructure("a a b")
            .addIngredient('a', Item.simple(ItemStack.of(Material.DIRT)))
            .addIngredient('b', Item.simple(ItemStack.of(Material.DIAMOND)))
            .build();
        var player = server.addPlayer();
        
        gui.setItem('a', Item.builder().setItemProvider(new ItemWrapper(ItemStack.of(Material.DIAMOND_BLOCK))));
        
        assertEquals(Material.DIAMOND_BLOCK, gui.getSlotElement(0).getItemStack(player).getType());
        assertEquals(Material.DIAMOND_BLOCK, gui.getSlotElement(1).getItemStack(player).getType());
        assertEquals(Material.DIAMOND, gui.getSlotElement(2).getItemStack(player).getType());
        assertNotSame(((SlotElement.Item)gui.getSlotElement(0)).item(), ((SlotElement.Item)gui.getSlotElement(1)).item());
    }
    
    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testSetItemSupplierUsingIngredientKey() {
        var gui = Gui.normal()
            .setStructure("a a b")
            .addIngredient('a', Item.simple(ItemStack.of(Material.DIRT)))
            .addIngredient('b', Item.simple(ItemStack.of(Material.DIAMOND)))
            .build();
        var player = server.addPlayer();
        
        Supplier<Item> supplier = () -> Item.simple(ItemStack.of(Material.DIAMOND_BLOCK));
        gui.setItem('a', supplier);
        
        assertEquals(Material.DIAMOND_BLOCK, gui.getSlotElement(0).getItemStack(player).getType());
        assertEquals(Material.DIAMOND_BLOCK, gui.getSlotElement(1).getItemStack(player).getType());
        assertEquals(Material.DIAMOND, gui.getSlotElement(2).getItemStack(player).getType());
        assertNotSame(((SlotElement.Item)gui.getSlotElement(0)).item(), ((SlotElement.Item)gui.getSlotElement(1)).item());
    }
    
    @SuppressWarnings("DataFlowIssue")
    @Test
    public void setInventoryUsingIngredientKey() {
        var gui = Gui.normal()
            .setStructure("x x x")
            .addIngredient('x', Item.simple(ItemStack.of(Material.DIRT)))
            .build();
        
        var inventory = new VirtualInventory(3);
        gui.setInventory('x', inventory);
        
        for (int i = 0; i < 3; i++) {
            assertSame(inventory, ((SlotElement.InventoryLink) gui.getSlotElement(i)).inventory());
            assertEquals(i, ((SlotElement.InventoryLink) gui.getSlotElement(i)).slot());
        }
    }
    
    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testSetGuiUsingIngredientKey() {
        var gui = Gui.normal()
            .setStructure("x x x")
            .addIngredient('x', Item.simple(ItemStack.of(Material.DIRT)))
            .build();
        
        var innerGui = Gui.empty(3, 1);
        gui.setGui('x', innerGui);
        
        for (int i = 0; i < 3; i++) {
            assertSame(innerGui, ((SlotElement.GuiLink) gui.getSlotElement(i)).gui());
            assertEquals(i, ((SlotElement.GuiLink) gui.getSlotElement(i)).slot());
        }
    }
    
}
