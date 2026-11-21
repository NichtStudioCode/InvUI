package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GuiBuilderTest {
    
    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
    }
    
    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    public void testRawValuesAreIsolatedBetweenBuilds() {
        var background = new ItemWrapper(ItemStack.of(Material.DIAMOND));
        var builder = Gui.builder()
            .setStructure("x x x")
            .setBackground(background)
            .setFrozen(false)
            .setIgnoreObscuredInventorySlots(false);
        
        var gui1 = builder.build();
        var gui2 = builder.build();
        
        gui1.setBackground(null);
        gui1.setFrozen(true);
        gui1.setIgnoreObscuredInventorySlots(true);
        
        assertNull(gui1.getBackground());
        assertSame(background, gui2.getBackground());
        assertTrue(gui1.isFrozen());
        assertFalse(gui2.isFrozen());
        assertTrue(gui1.isIgnoreObscuredInventorySlots());
        assertFalse(gui2.isIgnoreObscuredInventorySlots());
    }
    
    @Test
    public void testPropertiesAreSharedBetweenBuilds() {
        var background = MutableProperty.<ItemProvider>of(null);
        var frozen = MutableProperty.of(false);
        var ignoreObscuredInventorySlots = MutableProperty.of(false);
        var builder = Gui.builder()
            .setStructure("x x x")
            .setBackground(background)
            .setFrozen(frozen)
            .setIgnoreObscuredInventorySlots(ignoreObscuredInventorySlots);
        
        var gui1 = builder.build();
        var gui2 = builder.build();
        
        var itemProvider = new ItemWrapper(ItemStack.of(Material.DIAMOND));
        background.set(itemProvider);
        frozen.set(true);
        ignoreObscuredInventorySlots.set(true);
        
        assertSame(itemProvider, gui1.getBackground());
        assertSame(itemProvider, gui2.getBackground());
        assertTrue(gui1.isFrozen());
        assertTrue(gui2.isFrozen());
        assertTrue(gui1.isIgnoreObscuredInventorySlots());
        assertTrue(gui2.isIgnoreObscuredInventorySlots());
    }
    
    @Test
    public void testRawValuesAreIsolatedBetweenClones() {
        var builder = Gui.builder()
            .setStructure("x x x")
            .setFrozen(false);
        var clone = builder.clone();
        clone.setFrozen(true);
        
        assertFalse(builder.build().isFrozen());
        assertTrue(clone.build().isFrozen());
    }
    
    @Test
    public void testPagedContentIsIsolatedBetweenBuilds() {
        var item1 = Item.simple(ItemStack.of(Material.DIAMOND));
        var item2 = Item.simple(ItemStack.of(Material.DIRT));
        var builder = PagedGui.itemsBuilder()
            .setStructure("x x x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(List.of(item1));
        
        var gui1 = builder.build();
        var gui2 = builder.build();
        
        gui1.setContent(List.of(item2));
        
        assertSame(item2, gui1.getContent().getFirst());
        assertSame(item1, gui2.getContent().getFirst());
    }
    
    @Test
    public void testPagedContentPropertyIsSharedBetweenBuilds() {
        var item1 = Item.simple(ItemStack.of(Material.DIAMOND));
        var item2 = Item.simple(ItemStack.of(Material.DIRT));
        var content = MutableProperty.<List<? extends Item>>of(List.of(item1));
        var builder = PagedGui.itemsBuilder()
            .setStructure("x x x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(content);
        
        var gui1 = builder.build();
        var gui2 = builder.build();
        
        content.set(List.of(item2));
        
        assertSame(item2, gui1.getContent().getFirst());
        assertSame(item2, gui2.getContent().getFirst());
    }
    
    @Test
    public void testRawContentListIsNotSharedBetweenBuilds() {
        var item1 = Item.simple(ItemStack.of(Material.DIAMOND));
        var item2 = Item.simple(ItemStack.of(Material.DIRT));
        var content = new ArrayList<Item>();
        content.add(item1);
        var builder = PagedGui.itemsBuilder()
            .setStructure("x x x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(content);
        
        var gui1 = builder.build();
        var gui2 = builder.build();
        content.add(item2);
        
        assertEquals(1, gui1.getContent().size());
        assertEquals(1, gui2.getContent().size());
        assertSame(item1, gui1.getContent().getFirst());
        assertSame(item1, gui2.getContent().getFirst());
    }
    
    @Test
    public void testRawContentListIsCopiedOnClone() {
        var item1 = Item.simple(ItemStack.of(Material.DIAMOND));
        var item2 = Item.simple(ItemStack.of(Material.DIRT));
        var content = new ArrayList<Item>();
        content.add(item1);
        var builder = PagedGui.itemsBuilder()
            .setStructure("x x x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(content);
        var clone = builder.clone();
        
        content.add(item2);
        
        var gui = clone.build();
        assertEquals(1, gui.getContent().size());
        assertSame(item1, gui.getContent().getFirst());
    }
    
    @Test
    public void testRawTabsListIsNotSharedBetweenBuilds() {
        var tab1 = Gui.empty(1, 1);
        var tab2 = Gui.empty(1, 1);
        var tabs = new ArrayList<Gui>();
        tabs.add(tab1);
        var builder = TabGui.builder()
            .setStructure("x")
            .setTabs(tabs);
        
        var gui1 = builder.build();
        var gui2 = builder.build();
        tabs.add(tab2);
        
        assertEquals(1, gui1.getTabs().size());
        assertEquals(1, gui2.getTabs().size());
        assertSame(tab1, gui1.getTabs().getFirst());
        assertSame(tab1, gui2.getTabs().getFirst());
    }
    
    @Test
    public void testRawTabsListIsCopiedOnClone() {
        var tab1 = Gui.empty(1, 1);
        var tab2 = Gui.empty(1, 1);
        var tabs = new ArrayList<Gui>();
        tabs.add(tab1);
        var builder = TabGui.builder()
            .setStructure("x")
            .setTabs(tabs);
        var clone = builder.clone();
        
        tabs.add(tab2);
        
        var gui = clone.build();
        assertEquals(1, gui.getTabs().size());
        assertSame(tab1, gui.getTabs().getFirst());
    }
    
}
