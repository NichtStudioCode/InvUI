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
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ScrollGuiTest {
    
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
    public void testScrollHorizontalLineLength1() {
        var items = List.of(
            Item.simple(ItemStack.of(Material.STONE)),
            Item.simple(ItemStack.of(Material.DIRT)),
            Item.simple(ItemStack.of(Material.GRASS_BLOCK)),
            Item.simple(ItemStack.of(Material.COBBLESTONE)),
            Item.simple(ItemStack.of(Material.BARRIER))
        );
        
        var gui = ScrollGui.itemsBuilder()
            .setStructure(
                "x . .",
                "x . .",
                "x . ."
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(items)
            .build();
        
        assertEquals(items.get(0), gui.getItem(0, 0));
        assertEquals(items.get(1), gui.getItem(0, 1));
        assertEquals(items.get(2), gui.getItem(0, 2));
        
        gui.setLine(gui.getLine() + 1);
        
        assertEquals(items.get(1), gui.getItem(0, 0));
        assertEquals(items.get(2), gui.getItem(0, 1));
        assertEquals(items.get(3), gui.getItem(0, 2));
    }
    
    @Test
    public void testScrollVerticalLineLength1() {
        var items = List.of(
            Item.simple(ItemStack.of(Material.STONE)),
            Item.simple(ItemStack.of(Material.DIRT)),
            Item.simple(ItemStack.of(Material.GRASS_BLOCK)),
            Item.simple(ItemStack.of(Material.COBBLESTONE)),
            Item.simple(ItemStack.of(Material.BARRIER))
        );
        
        var gui = ScrollGui.itemsBuilder()
            .setStructure(
                "x x x",
                ". . .",
                ". . ."
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_VERTICAL)
            .setContent(items)
            .build();
        
        assertEquals(items.get(0), gui.getItem(0, 0));
        assertEquals(items.get(1), gui.getItem(1, 0));
        assertEquals(items.get(2), gui.getItem(2, 0));
        
        gui.setLine(gui.getLine() + 1);
        
        assertEquals(items.get(1), gui.getItem(0, 0));
        assertEquals(items.get(2), gui.getItem(1, 0));
        assertEquals(items.get(3), gui.getItem(2, 0));
    }
    
    @Test
    public void testScrollContentChangeWithApplyStructure() {
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
        var gui = ScrollGui.itemsBuilder()
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
    
    @ParameterizedTest
    @ValueSource(ints = {0, 10, 99, 1000})
    public void testLineProperty(int itemCount) {
        int maxLine = Math.max(0, (int) Math.ceil(itemCount / 3.0) - 1);
        
        var line = MutableProperty.of(0);
        
        var gui = ScrollGui.itemsBuilder()
            .setLine(line)
            .setStructure(
                ". . .",
                "x x x",
                ". . ."
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(IntStream.range(0, itemCount).mapToObj(i -> Item.simple(ItemStack.of(Material.DIAMOND))).toList())
            .build();
        
        // check that property value is updated on line change
        assertEquals(0, line.get());
        gui.setLine(Integer.MAX_VALUE);
        assertEquals(maxLine, line.get());
        
        // check that line value is coerced into valid range
        line.set(0);
        assertEquals(0, gui.getLine());
        line.set(Integer.MAX_VALUE);
        assertEquals(maxLine, gui.getLine());
        assertEquals(maxLine, line.get());
    }
    
    @Test
    public void testContentProperty() {
        MutableProperty<List<Item>> content = MutableProperty.of(List.of());
        
        var gui = ScrollGui.itemsBuilder()
            .setStructure("x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(content)
            .build();
        
        assertNull(gui.getItem(0));
        
        var items = List.of(
            Item.simple(ItemStack.of(Material.STONE)),
            Item.simple(ItemStack.of(Material.DIRT)),
            Item.simple(ItemStack.of(Material.COBWEB))
        );
        content.set(items);
        
        assertEquals(gui.getItem(0), items.getFirst());
        for (int i = 0; i < items.size(); i++) {
            gui.setLine(i);
            assertEquals(gui.getItem(0), items.get(i));
        }
        
        content.set(List.of());
        assertNull(gui.getItem(0));
        
        gui.setContent(MutableProperty.of(List.of()));
        content.set(items);
        assertNull(gui.getItem(0));
    }
    
}
