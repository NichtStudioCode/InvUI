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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
        var gui = PagedGui.itemsBuilder()
            .setStructure(s1)
            .setContent(content)
            .build();
        
        // validate assumptions for s1
        for (int i = 0; i < 6; i++) {
            var element = gui.getSlotElement(i);
            assertInstanceOf(SlotElement.Item.class, element);
            
            var item = ((SlotElement.Item) element).item();
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
            assertNull(gui.getSlotElement(i));
        }
        for (int i = 3; i < 6; i++) {
            var element = gui.getSlotElement(i);
            assertInstanceOf(SlotElement.Item.class, element);
            
            var item = ((SlotElement.Item) element).item();
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
    public void testPageProperty(int itemCount) {
        int maxPage = Math.max(0, (int) Math.ceil(itemCount / 3.0) - 1);
        
        var page = MutableProperty.of(0);
        
        var gui = PagedGui.itemsBuilder()
            .setPage(page)
            .setStructure(
                ". . .",
                "x x x",
                ". . ."
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(IntStream.range(0, itemCount).mapToObj(i -> Item.simple(ItemStack.of(Material.DIAMOND))).toList())
            .build();
        
        // check that property value is updated on page change
        assertEquals(0, page.get());
        gui.setPage(Integer.MAX_VALUE);
        assertEquals(maxPage, page.get());
        
        // check that page value is coerced into valid range
        page.set(0);
        assertEquals(0, gui.getPage());
        page.set(Integer.MAX_VALUE);
        assertEquals(maxPage, gui.getPage());
        assertEquals(maxPage, page.get());
    }
    
    @Test
    public void testContentProperty() {
        MutableProperty<List<? extends Item>> content = MutableProperty.of(List.of());
        
        var gui = PagedGui.itemsBuilder()
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
            gui.setPage(i);
            assertEquals(gui.getItem(0), items.get(i));
        }
        
        content.set(List.of());
        assertNull(gui.getItem(0));
    }
    
    @Test
    public void testPageChangeHandler() {
        var prevPage = new AtomicInteger();
        var page = new AtomicInteger();
        
        var gui = PagedGui.itemsBuilder()
            .setStructure(
                "x x x",
                "x x x", 
                "x x x"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(exampleItems())
            .addPageChangeHandler((old, now) -> {
                prevPage.set(old);
                page.set(now);
            })
            .build();
        
        gui.setPage(10);
        assertEquals(0, prevPage.get());
        assertEquals(10, page.get());
        
        gui.setPage(-999); // out of bounds, coerced to 0
        assertEquals(10, prevPage.get());
        assertEquals(0, page.get());
        
        gui.setPage(0); // same page, ignored
        assertEquals(10, prevPage.get());
        assertEquals(0, page.get());
    }
    
    @Test
    public void testPageChangeHandlerWithPageProperty() {
        var prevPage = new AtomicInteger();
        var page = new AtomicInteger();
        
        var pageProperty = MutableProperty.of(0);
        
        PagedGui.itemsBuilder()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(exampleItems())
            .setPage(pageProperty)
            .addPageChangeHandler((old, now) -> {
                prevPage.set(old);
                page.set(now);
            })
            .build();
        
        pageProperty.set(10);
        assertEquals(0, prevPage.get());
        assertEquals(10, page.get());
        
        pageProperty.set(-999); // out of bounds, coerced to 0
        assertEquals(10, prevPage.get());
        assertEquals(0, page.get());
        assertEquals(0, pageProperty.get());
        
        pageProperty.set(0); // same page, ignored
        assertEquals(10, prevPage.get());
        assertEquals(0, page.get());
    }
    
    @Test
    public void testPageCountChangeHandler() {
        var pageCount = new AtomicInteger(-1);
        var pageCountChangeCount = new AtomicInteger(0);
        
        var gui = PagedGui.itemsBuilder()
            .setStructure("x x x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addPageCountChangeHandler((from, to) -> {
                pageCount.set(to);
                pageCountChangeCount.incrementAndGet();
            })
            .build();
        
        assertEquals(-1, pageCount.get());
        assertEquals(0, pageCountChangeCount.get());
        
        gui.setContent(IntStream.range(0, 7).mapToObj(i -> Item.builder().build()).toList());
        assertEquals(3, pageCount.get());
        assertEquals(1, pageCountChangeCount.get());
        
        // no page count change, no handler invocation
        gui.setContent(IntStream.range(0, 7).mapToObj(i -> Item.builder().build()).toList());
        assertEquals(3, pageCount.get());
        assertEquals(1, pageCountChangeCount.get());
        
        gui.setContent(List.of());
        assertEquals(0, pageCount.get());
        assertEquals(2, pageCountChangeCount.get());
    }
    
    @Test
    public void testPageCountProperty() {
        var gui = PagedGui.itemsBuilder()
            .setStructure("x x x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .build();
        var pageCount = gui.getPageCountProperty();
        
        assertEquals(0, pageCount.get());
        
        gui.setContent(IntStream.range(0, 7).mapToObj(i -> Item.builder().build()).toList());
        assertEquals(3, pageCount.get());
     
        gui.setContent(List.of());
        assertEquals(0, pageCount.get());
    }
    
    private List<? extends Item> exampleItems() {
        return Arrays.stream(Material.values())
            .filter(m -> !m.isLegacy() && m.isItem())
            .map(m -> Item.simple(ItemStack.of(m)))
            .toList();
    }
    
}
