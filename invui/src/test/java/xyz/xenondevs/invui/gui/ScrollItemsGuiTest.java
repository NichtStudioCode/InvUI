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
import static xyz.xenondevs.invui.Utils.assertItems;

public class ScrollItemsGuiTest {
    
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
        var items = IntStream.range(0, 10)
            .mapToObj(i -> Item.simple(ItemStack.of(Material.DIAMOND)))
            .toList();
        
        var b = Item.simple(ItemStack.of(Material.DIAMOND));
        var gui = ScrollGui.itemsBuilder()
            .setStructure(
                "b b b b b",
                "b . x x b",
                "b x . x b",
                "b x x . b",
                "b b b b b"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('b', b)
            .setContent(items)
            .build();
        
        assertEquals(1, gui.getMaxLine());
        assertEquals(4, gui.getLineCount());
        
        assertItems(
            gui,
            b, b, b, b, b,
            b, null, items.get(1), items.get(2), b,
            b, items.get(3), null, items.get(5), b,
            b, items.get(6), items.get(7), null, b,
            b, b, b, b, b
        );
        
        gui.setLine(1);
        
        assertItems(
            gui,
            b, b, b, b, b,
            b, null, items.get(4), items.get(5), b,
            b, items.get(6), null, items.get(8), b,
            b, items.get(9), null, null, b,
            b, b, b, b, b
        );
    }
    
    @Test
    public void testWithNonContinuousVerticalLines() {
        var items = IntStream.range(0, 10)
            .mapToObj(i -> Item.simple(ItemStack.of(Material.DIAMOND)))
            .toList();
            
        var b = Item.simple(ItemStack.of(Material.DIAMOND));
        var gui = ScrollGui.itemsBuilder()
            .setStructure(
                "b b b b b",
                "b . x x b",
                "b x . x b",
                "b x x . b",
                "b b b b b"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_VERTICAL)
            .addIngredient('b', b)
            .setContent(items)
            .build();
        
        assertEquals(1, gui.getMaxLine());
        assertEquals(4, gui.getLineCount());
        
        assertItems(
            gui,
            b, b, b, b, b,
            b, null, items.get(3), items.get(6), b,
            b, items.get(1), null, items.get(7), b,
            b, items.get(2), items.get(5), null, b,
            b, b, b, b, b
        );
        
        gui.setLine(1);
        
        assertItems(
            gui,
            b, b, b, b, b,
            b, null, items.get(6), items.get(9), b,
            b, items.get(4), null, null, b,
            b, items.get(5), items.get(8), null, b,
            b, b, b, b, b
        );
    }
    
    @ValueSource(booleans = {true, false})
    @ParameterizedTest()
    public void testContentRemoved(boolean horizontalLines) {
        var m = horizontalLines ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL;
        var gui = ScrollGui.itemsBuilder()
            .setStructure("x x x")
            .addIngredient('x', m)
            .setContent(IntStream.range(0, 99).mapToObj(i -> Item.simple(ItemStack.of(Material.DIAMOND))).toList())
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
    public void testScrollItemsInitWithBuilder() {
        testScrollItemsInit(content -> ScrollGui.itemsBuilder()
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
    public void testScrollItemsInitWithStaticFactoryFunction() {
        testScrollItemsInit(content ->
            ScrollGui.ofItems(
                3, 3,
                content,
                SlotUtils.toSlotList(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, 3),
                ScrollGui.LineOrientation.HORIZONTAL
            )
        );
    }
    
    private void testScrollItemsInit(Function<? super List<Item>, ? extends Gui> createGui) {
        var content = List.of(
            Item.simple(ItemStack.of(Material.DIAMOND)),
            Item.simple(ItemStack.of(Material.DIAMOND)),
            Item.simple(ItemStack.of(Material.DIAMOND)),
            Item.simple(ItemStack.of(Material.DIAMOND))
        );
        
        var gui = createGui.apply(content);
        
        for (int i = 0; i < 4; i++) {
            assertSame(content.get(i), gui.getItem(i), "i=" + i);
        }
        for (int i = 4; i < 9; i++) {
            assertNull(gui.getSlotElement(i), "i=" + i);
        }
    }
    
}
