package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static xyz.xenondevs.invui.Utils.assertSlotElements;
import static xyz.xenondevs.invui.Utils.il;

public class PagedInventoriesGuiTest {
    
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
    public void testWithNonContinuousHorizontalLines() {
        var i1 = new VirtualInventory(5);
        var i2 = new VirtualInventory(5);
        var i3 = new VirtualInventory(6);
        var b = new SlotElement.Item(Item.simple(ItemStack.of(Material.BLACK_STAINED_GLASS)));
        var gui = PagedGui.inventoriesBuilder()
            .setStructure(
                "b b b b b",
                "b . x x b",
                "b x . x b",
                "b x x . b",
                "b b b b b"
            )
            .addIngredient('b', b)
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(List.of(i1, i2, i3))
            .build();
        
        assertEquals(3, gui.getPageCount());
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, il(i1, 0), il(i1, 1), b,
            b, il(i1, 2), null, il(i1, 3), b,
            b, il(i1, 4), il(i2, 0), null, b,
            b, b, b, b, b
        );
        
        gui.setPage(1);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, il(i2, 1), il(i2, 2), b,
            b, il(i2, 3), null, il(i2, 4), b,
            b, il(i3, 0), il(i3, 1), null, b,
            b, b, b, b, b
        );
        
        gui.setPage(2);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, il(i3, 2), il(i3, 3), b,
            b, il(i3, 4), null, il(i3, 5), b,
            b, null, null, null, b,
            b, b, b, b, b
        );
    }
    
    @Test
    public void testWithNonContinuousVerticalLines() {
        var i1 = new VirtualInventory(5);
        var i2 = new VirtualInventory(5);
        var i3 = new VirtualInventory(6);
        var b = new SlotElement.Item(Item.simple(ItemStack.of(Material.BLACK_STAINED_GLASS)));
        var gui = PagedGui.inventoriesBuilder()
            .setStructure(
                "b b b b b",
                "b . x x b",
                "b x . x b",
                "b x x . b",
                "b b b b b"
            )
            .addIngredient('b', b)
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_VERTICAL)
            .setContent(List.of(i1, i2, i3))
            .build();
        
        assertEquals(3, gui.getPageCount());
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, il(i1, 2), il(i1, 4), b,
            b, il(i1, 0), null, il(i2, 0), b,
            b, il(i1, 1), il(i1, 3), null, b,
            b, b, b, b, b
        );
        
        gui.setPage(1);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, il(i2, 3), il(i3, 0), b,
            b, il(i2, 1), null, il(i3, 1), b,
            b, il(i2, 2), il(i2, 4), null, b,
            b, b, b, b, b
        );
        
        gui.setPage(2);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, il(i3, 4), null, b,
            b, il(i3, 2), null, null, b,
            b, il(i3, 3), il(i3, 5), null, b,
            b, b, b, b, b
        );
    }
    
    @ValueSource(booleans = {true, false})
    @ParameterizedTest()
    public void testContentRemoved(boolean horizontalLines) {
        var m = horizontalLines ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL;
        var gui = PagedGui.inventoriesBuilder()
            .setStructure("x x x")
            .addIngredient('x', m)
            .setContent(List.of(new VirtualInventory(99)))
            .build();
        
        assertNotNull(gui.getSlotElement(0));
        assertNotNull(gui.getSlotElement(1));
        assertNotNull(gui.getSlotElement(2));
        
        gui.setContent(List.of());
        
        assertNull(gui.getSlotElement(0));
        assertNull(gui.getSlotElement(1));
        assertNull(gui.getSlotElement(2));
    }
    
    @Test
    public void testPagedInventoriesInitWithBuilder() {
        testPagedInventoriesInit(content ->
            PagedGui.inventoriesBuilder()
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
    public void testPagedInventoriesInitWithStaticFactoryFunction() {
        testPagedInventoriesInit(content ->
            PagedGui.ofInventories(
                3, 3,
                content,
                SlotUtils.toSlotList(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, 3)
            )
        );
    }
    
    private void testPagedInventoriesInit(Function<? super List<Inventory>, ? extends Gui> createGui) {
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
    
}
