package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
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
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class TabGuiTest {
    
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
    public void testTabSwitching() {
        var tabs = List.of(
            Gui.builder()
                .setStructure("xxx")
                .addIngredient('x', Item.simple(ItemStack.of(Material.ANDESITE)))
                .build(),
            Gui.builder()
                .setStructure("xxx")
                .addIngredient('x', Item.simple(ItemStack.of(Material.BARRIER)))
                .build(),
            Gui.builder()
                .setStructure("xxx")
                .addIngredient('x', Item.simple(ItemStack.of(Material.COBBLESTONE)))
                .build()
        );
        
        var gui = TabGui.builder()
            .setStructure(
                "# # #",
                "x x x"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setTabs(tabs)
            .build();
        
        assertEquals(0, gui.getTab());
        
        for (int tab = -1; tab <= tabs.size(); tab++) {
            gui.setTab(tab);
            int coercedTab = Math.max(0, Math.min(2, tab));
            assertEquals(coercedTab, gui.getTab());
            for (int itemIdx = 3; itemIdx < 6; itemIdx++) {
                var slotElement = gui.getSlotElement(itemIdx);
                assertInstanceOf(SlotElement.GuiLink.class, slotElement);
                assertEquals(
                    tabs.get(coercedTab),
                    ((SlotElement.GuiLink)slotElement).gui(),
                    "tab=" + tab + ", itemIdx=" + itemIdx
                );
                assertEquals(
                    itemIdx - 3, 
                    ((SlotElement.GuiLink)slotElement).slot(),
                    "tab=" + tab + ", itemIdx=" + itemIdx
                );
            }
        }
    }
    
    @Test
    public void testTabsProperty() {
        MutableProperty<List<@Nullable Gui>> tabs = MutableProperty.of(List.of());
        
        var gui = TabGui.builder()
            .setStructure("x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setTabs(tabs)
            .build();
        
        assertNull(gui.getSlotElement(0));
        
        var tabsList = List.of(
            Gui.empty(1, 1),
            Gui.empty(1, 1),
            Gui.empty(1, 1)
        );
        tabs.set(tabsList);
        
        assertEquals(
            ((SlotElement.GuiLink) Objects.requireNonNull(gui.getSlotElement(0))).gui(),
            tabsList.getFirst()
        );
        for (int i = 0; i < tabsList.size(); i++) {
            gui.setTab(i);
            assertEquals(
                ((SlotElement.GuiLink) Objects.requireNonNull(gui.getSlotElement(0))).gui(),
                tabsList.get(i)
            );
        }
        
        tabs.set(List.of());
        assertNull(gui.getItem(0));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    public void testTabProperty(int tabCount) {
        var tab = MutableProperty.of(0);
        var gui = TabGui.builder()
            .setTab(tab)
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setTabs(IntStream.range(0, tabCount).mapToObj(i -> Gui.empty(3, 3)).toList())
            .build();
        
        // check that property value is updated on tab change
        assertEquals(Math.min(tabCount - 1, 0), tab.get());
        gui.setTab(Integer.MAX_VALUE);
        assertEquals(tabCount - 1, tab.get());
        
        // check that tab value is coerced into valid range
        tab.set(0);
        assertEquals(Math.min(tabCount - 1, 0), gui.getTab());
        tab.set(Integer.MAX_VALUE);
        assertEquals(tabCount - 1, gui.getTab());
        assertEquals(tabCount - 1, tab.get());
    }
    
    @Test
    public void testNoTabs() {
        // validate that tab guis can be created without tabs
        var gui = TabGui.builder()
            .setStructure(
                "# # #",
                "x x x",
                "x x x"
            )
            .addIngredient('#', ItemStack.of(Material.BLACK_STAINED_GLASS_PANE))
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .build();
        
        assertEquals(-1, gui.getTab());
        for (int i = 0; i < 3; i++) {
            assertNotNull(gui.getItem(i));
        }
        for (int i = 3; i < 9; i++) {
            assertNull(gui.getSlotElement(i));
        }
        
        // test that tabs can be added later and the gui will switch to the first tab
        gui.setTabs(List.of(Gui.empty(3, 3), Gui.empty(3, 3)));
        assertEquals(0, gui.getTab());
        gui.setTab(Integer.MAX_VALUE);
        assertEquals(1, gui.getTab());
        for (int i = 3; i < 9; i++) {
            assertInstanceOf(SlotElement.GuiLink.class, gui.getSlotElement(i));
        }
        
        // test that all tabs can be removed again
        gui.setTabs(List.of());
        assertEquals(-1, gui.getTab());
        for (int i = 3; i < 9; i++) {
            assertNull(gui.getSlotElement(i));
        }
    }
    
    
    @Test
    public void testTabContentChangeWithApplyStructure() {
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
        
        var tab = Gui.empty(9, 2);
        tab.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        
        var gui = TabGui.builder()
            .setStructure(s1)
            .setTabs(List.of(tab))
            .build();
        
        // validate assumptions for s1
        for (int i = 0; i < 6; i++) {
            var element = gui.getSlotElement(i);
            assertInstanceOf(SlotElement.GuiLink.class, element);
            assertSame(tab, ((SlotElement.GuiLink) element).gui());
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
            assertInstanceOf(SlotElement.GuiLink.class, element);
            assertSame(tab, ((SlotElement.GuiLink) element).gui());
        }
        for (int i = 6; i < 9; i++) {
            var element = gui.getSlotElement(i);
            assertNotNull(element);
            
            var itemStack = element.getItemStack(player);
            assertNotNull(itemStack);
            assertEquals(Material.BLACK_STAINED_GLASS, itemStack.getType());
        }
    }
    
}
