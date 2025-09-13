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
        inner.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        
        var outer = Gui.builder()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inner)
            .build();
        
        for (int i = 0; i < 9; i++) {
            assertGuiLink(outer, i, inner, i);
        }
    }
    
    @Test
    public void testGuiBuilderGuiIngredientDisplaced() {
        var inner = Gui.empty(2, 2);
        inner.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        
        var outer = Gui.builder()
            .setStructure(
                ". . .",
                ". x x",
                ". x x"
            )
            .addIngredient('x', inner)
            .build();
        
        assertGuiLink(outer, 4, inner, 0);
        assertGuiLink(outer, 5, inner, 1);
        assertGuiLink(outer, 7, inner, 2);
        assertGuiLink(outer, 8, inner, 3);
    }
    
    @Test
    public void testGuiBuilderGuiIngredientWithOffset() {
        var inner = Gui.empty(3, 3);
        inner.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        
        var outer = Gui.builder()
            .setStructure(
                ". . .",
                "x x x",
                ". . ."
            )
            .addIngredient('x', inner, 0, 1)
            .build();
        
        for (int i = 3; i < 6; i++) {
            assertGuiLink(outer, i, inner, i);
        }
    }
    
    @Test
    public void testGuiBuilderGuiIngredientWithHoles() {
        var inner = Gui.empty(3, 3);
        inner.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        
        var outer = Gui.builder()
            .setStructure(
                ". x x",
                "x . x",
                "x x ."
            )
            .addIngredient('x', inner)
            .build();
        
        assertGuiLink(outer, 1, inner, 1);
        assertGuiLink(outer, 2, inner, 2);
        assertGuiLink(outer, 3, inner, 3);
        assertGuiLink(outer, 5, inner, 5);
        assertGuiLink(outer, 6, inner, 6);
        assertGuiLink(outer, 7, inner, 7);
    }
    
    @Test
    public void testGuiBuilderGuiIngredientTargetSlotOutOfBounds() {
        var inner = Gui.empty(2, 2);
        inner.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        
        assertThrows(
            IndexOutOfBoundsException.class,
            () -> Gui.builder()
                .setStructure(
                    "x . .",
                    ". . .",
                    ". . x"
                )
                .addIngredient('x', inner, 1, 2)
                .build()
        );
    }
    
    @Test
    public void testGuiBuilderGuiIngredientNotEnoughSlots() {
        var inner = Gui.empty(3, 3);
        inner.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        
        var outer = Gui.builder()
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
        inner.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        
        assertThrows(
            IndexOutOfBoundsException.class,
            () -> Gui.builder()
                .setStructure(
                    "x x x",
                    "x x x",
                    "x x x"
                )
                .addIngredient('x', inner)
                .build()
        );
    }
    
    @Test
    public void testGuiBuilderGuiIngredientInvokedTwice() {
        var inner = Gui.empty(3, 3);
        inner.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        
        var builder = Gui.builder()
            .setStructure(
                ". . .",
                "x x x",
                ". . ."
            )
            .addIngredient('x', inner);
        
        var gui1 = builder.build();
        assertNull(gui1.getSlotElement(0));
        assertNull(gui1.getSlotElement(1));
        assertNull(gui1.getSlotElement(2));
        assertGuiLink(gui1, 3, inner, 0);
        assertGuiLink(gui1, 4, inner, 1);
        assertGuiLink(gui1, 5, inner, 2);
        assertNull(gui1.getSlotElement(6));
        assertNull(gui1.getSlotElement(7));
        assertNull(gui1.getSlotElement(8));
        
        builder.addIngredient('a', Markers.CONTENT_LIST_SLOT_HORIZONTAL); // modify builder to invalidate cache
        
        var gui2 = builder.build();
        assertNull(gui2.getSlotElement(0));
        assertNull(gui2.getSlotElement(1));
        assertNull(gui2.getSlotElement(2));
        assertGuiLink(gui2, 3, inner, 0);
        assertGuiLink(gui2, 4, inner, 1);
        assertGuiLink(gui2, 5, inner, 2);
        assertNull(gui2.getSlotElement(6));
        assertNull(gui2.getSlotElement(7));
        assertNull(gui2.getSlotElement(8));
    }
    
    private void assertGuiLink(Gui gui, int from, Gui linked, int linkedSlot) {
        var element = gui.getSlotElement(from);
        assertInstanceOf(SlotElement.GuiLink.class, element);
        
        var guiLink = (SlotElement.GuiLink) element;
        assertSame(linked, guiLink.gui());
        assertEquals(linkedSlot, guiLink.slot());
    }
    
    @Test
    public void testGuiBuilderInventoryIngredient() {
        var inv = new VirtualInventory(9);
        
        var outer = Gui.builder()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', inv)
            .build();
        
        for (int i = 0; i < 9; i++) {
            assertInvLink(outer, i, inv, i);
        }
    }
    
    @Test
    public void testGuiBuilderInventoryIngredientNotEnoughSlots() {
        var inv = new VirtualInventory(9);
        
        var outer = Gui.builder()
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
        
        assertThrows(IndexOutOfBoundsException.class, () ->
            Gui.builder()
                .setStructure(
                    "x x x",
                    "x x x",
                    "x x x"
                )
                .addIngredient('x', inv)
                .build()
        );
    }
    
    @Test
    public void testGuiBuilderInventoryIngredientInvokedTwice() {
        var inv = new VirtualInventory(9);
        
        var builder = Gui.builder()
            .setStructure(
                ". . .",
                "x x x",
                ". . ."
            )
            .addIngredient('x', inv);
        
        var gui1 = builder.build();
        assertNull(gui1.getSlotElement(0));
        assertNull(gui1.getSlotElement(1));
        assertNull(gui1.getSlotElement(2));
        assertInvLink(gui1, 3, inv, 0);
        assertInvLink(gui1, 4, inv, 1);
        assertInvLink(gui1, 5, inv, 2);
        assertNull(gui1.getSlotElement(6));
        assertNull(gui1.getSlotElement(7));
        assertNull(gui1.getSlotElement(8));
        
        builder.addIngredient('a', Markers.CONTENT_LIST_SLOT_HORIZONTAL); // modify builder to invalidate cache
        
        var gui2 = builder.build();
        assertNull(gui2.getSlotElement(0));
        assertNull(gui2.getSlotElement(1));
        assertNull(gui2.getSlotElement(2));
        assertInvLink(gui2, 3, inv, 0);
        assertInvLink(gui2, 4, inv, 1);
        assertInvLink(gui2, 5, inv, 2);
        assertNull(gui2.getSlotElement(6));
        assertNull(gui2.getSlotElement(7));
        assertNull(gui2.getSlotElement(8));
    }
    
    private void assertInvLink(Gui gui, int from, Inventory linked, int linkedSlot) {
        var element = gui.getSlotElement(from);
        assertInstanceOf(SlotElement.InventoryLink.class, element);
        
        var invLink = (SlotElement.InventoryLink) element;
        assertSame(linked, invLink.inventory());
        assertEquals(linkedSlot, invLink.slot());
    }
    
    @Test
    public void testGuiBuilderSlotElementSupplierIngredient() {
        var delegatedTo = Gui.empty(3, 2);
        var gui = Gui.builder()
            .setStructure(
                "x . x",
                ". x x"
            )
            .addIngredient('x', slots -> {
                assertEquals(
                    slots,
                    List.of(
                        new Slot(0, 0),
                        new Slot(2, 0),
                        new Slot(1, 1),
                        new Slot(2, 1)
                    )
                );
                return slots.stream()
                    .map(s -> new SlotElement.GuiLink(delegatedTo, s.y() * delegatedTo.getWidth() + s.x()))
                    .toList();
            })
            .build();
        
        assertEquals(gui.getSlotElement(0), new SlotElement.GuiLink(delegatedTo, 0));
        assertNull(gui.getSlotElement(1));
        assertEquals(gui.getSlotElement(2), new SlotElement.GuiLink(delegatedTo, 2));
        assertNull(gui.getSlotElement(3));
        assertEquals(gui.getSlotElement(4), new SlotElement.GuiLink(delegatedTo, 4));
        assertEquals(gui.getSlotElement(5), new SlotElement.GuiLink(delegatedTo, 5));
    }
    
    @Test
    public void testGetSlots() {
        var gui = Gui.builder()
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
        var gui = Gui.builder()
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
        var gui = Gui.builder()
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
        var gui = Gui.builder()
            .setStructure("a a b")
            .addIngredient('a', Item.simple(ItemStack.of(Material.DIRT)))
            .addIngredient('b', Item.simple(ItemStack.of(Material.DIAMOND)))
            .build();
        var player = server.addPlayer();
        
        gui.setItem('a', Item.builder().setItemProvider(new ItemWrapper(ItemStack.of(Material.DIAMOND_BLOCK))));
        
        assertEquals(Material.DIAMOND_BLOCK, gui.getSlotElement(0).getItemStack(player).getType());
        assertEquals(Material.DIAMOND_BLOCK, gui.getSlotElement(1).getItemStack(player).getType());
        assertEquals(Material.DIAMOND, gui.getSlotElement(2).getItemStack(player).getType());
        assertNotSame(((SlotElement.Item) gui.getSlotElement(0)).item(), ((SlotElement.Item) gui.getSlotElement(1)).item());
    }
    
    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testSetItemSupplierUsingIngredientKey() {
        var gui = Gui.builder()
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
        assertNotSame(((SlotElement.Item) gui.getSlotElement(0)).item(), ((SlotElement.Item) gui.getSlotElement(1)).item());
    }
    
    @SuppressWarnings("DataFlowIssue")
    @Test
    public void setInventoryUsingIngredientKey() {
        var gui = Gui.builder()
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
        var gui = Gui.builder()
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
    
    @Test
    public void testSetSlotElementSupplierUsingIngredientKey() {
        var delegatedTo = Gui.empty(3, 2);
        var gui = Gui.builder()
            .setStructure(
                "x . x",
                ". x x"
            ).build();
        
        for (int i = 0; i < 6; i++) {
            assertNull(gui.getSlotElement(i));
        }
        
        gui.setSlotElement('x', slots -> {
            assertEquals(
                slots,
                List.of(
                    new Slot(0, 0),
                    new Slot(2, 0),
                    new Slot(1, 1),
                    new Slot(2, 1)
                )
            );
            return slots.stream()
                .map(s -> new SlotElement.GuiLink(delegatedTo, s.y() * delegatedTo.getWidth() + s.x()))
                .toList();
        });
        
        assertEquals(gui.getSlotElement(0), new SlotElement.GuiLink(delegatedTo, 0));
        assertNull(gui.getSlotElement(1));
        assertEquals(gui.getSlotElement(2), new SlotElement.GuiLink(delegatedTo, 2));
        assertNull(gui.getSlotElement(3));
        assertEquals(gui.getSlotElement(4), new SlotElement.GuiLink(delegatedTo, 4));
        assertEquals(gui.getSlotElement(5), new SlotElement.GuiLink(delegatedTo, 5));
    }
    
}
