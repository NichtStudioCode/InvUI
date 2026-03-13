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
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static xyz.xenondevs.invui.Utils.assertSlotElements;

public class PagedSlotElementsGuiTest {
    
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
        var slotElements = IntStream.range(0, 16)
            .mapToObj(i -> new SlotElement.Item(Item.simple(ItemStack.of(Material.DIAMOND))))
            .toList();
        var b = new SlotElement.Item(Item.simple(ItemStack.of(Material.BLACK_STAINED_GLASS)));
        var gui = PagedGui.slotElementsBuilder()
            .setStructure(
                "b b b b b",
                "b . x x b",
                "b x . x b",
                "b x x . b",
                "b b b b b"
            )
            .addIngredient('b', b)
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(slotElements)
            .build();
        
        assertEquals(3, gui.getPageCount());
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, slotElements.get(0), slotElements.get(1), b,
            b, slotElements.get(2), null, slotElements.get(3), b,
            b, slotElements.get(4), slotElements.get(5), null, b,
            b, b, b, b, b
        );
        
        gui.setPage(1);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, slotElements.get(6), slotElements.get(7), b,
            b, slotElements.get(8), null, slotElements.get(9), b,
            b, slotElements.get(10), slotElements.get(11), null, b,
            b, b, b, b, b
        );
        
        gui.setPage(2);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, slotElements.get(12), slotElements.get(13), b,
            b, slotElements.get(14), null, slotElements.get(15), b,
            b, null, null, null, b,
            b, b, b, b, b
        );
    }
    
    @Test
    public void testWithNonContinuousVerticalLines() {
        var slotElements = IntStream.range(0, 16)
            .mapToObj(i -> new SlotElement.Item(Item.simple(ItemStack.of(Material.DIAMOND))))
            .toList();
        var b = new SlotElement.Item(Item.simple(ItemStack.of(Material.BLACK_STAINED_GLASS)));
        var gui = PagedGui.slotElementsBuilder()
            .setStructure(
                "b b b b b",
                "b . x x b",
                "b x . x b",
                "b x x . b",
                "b b b b b"
            )
            .addIngredient('b', b)
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_VERTICAL)
            .setContent(slotElements)
            .build();
        
        assertEquals(3, gui.getPageCount());
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, slotElements.get(2), slotElements.get(4), b,
            b, slotElements.get(0), null, slotElements.get(5), b,
            b, slotElements.get(1), slotElements.get(3), null, b,
            b, b, b, b, b
        );
        
        gui.setPage(1);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, slotElements.get(8), slotElements.get(10), b,
            b, slotElements.get(6), null, slotElements.get(11), b,
            b, slotElements.get(7), slotElements.get(9), null, b,
            b, b, b, b, b
        );
        
        gui.setPage(2);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, slotElements.get(14), null, b,
            b, slotElements.get(12), null, null, b,
            b, slotElements.get(13), slotElements.get(15), null, b,
            b, b, b, b, b
        );
    }
    
    @ValueSource(booleans = {true, false})
    @ParameterizedTest
    public void testContentRemoved(boolean horizontalLines) {
        var m = horizontalLines ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL;
        var gui = PagedGui.slotElementsBuilder()
            .setStructure("x x x")
            .addIngredient('x', m)
            .setContent(IntStream.range(0, 99)
                .mapToObj(i -> new SlotElement.Item(Item.simple(ItemStack.of(Material.DIAMOND))))
                .toList())
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
    public void testPagedSlotElementsInitWithBuilder() {
        testPagedSlotElementsInit(content -> PagedGui.slotElementsBuilder()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(content)
            .build());
    }
    
    @Test
    public void testPagedSlotElementsInitWithStaticFactoryFunction() {
        testPagedSlotElementsInit(content ->
            PagedGui.ofSlotElements(
                3, 3,
                content,
                SlotUtils.toSlotList(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, 3)
            )
        );
    }
    
    private void testPagedSlotElementsInit(Function<? super List<SlotElement>, ? extends Gui> createGui) {
        var content = List.<SlotElement>of(
            new SlotElement.Item(Item.simple(ItemStack.of(Material.DIAMOND))),
            new SlotElement.Item(Item.simple(ItemStack.of(Material.DIAMOND))),
            new SlotElement.Item(Item.simple(ItemStack.of(Material.DIAMOND))),
            new SlotElement.Item(Item.simple(ItemStack.of(Material.DIAMOND)))
        );
        
        var gui = createGui.apply(content);
        
        for (int i = 0; i < 4; i++) {
            assertSame(content.get(i), gui.getSlotElement(i), "i=" + i);
        }
        for (int i = 4; i < 9; i++) {
            assertNull(gui.getSlotElement(i), "i=" + i);
        }
    }
    
}

