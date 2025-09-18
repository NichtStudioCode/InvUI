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

import static org.junit.jupiter.api.Assertions.*;
import static xyz.xenondevs.invui.Utils.assertSlotElements;
import static xyz.xenondevs.invui.Utils.gl;

public class ScrollGuisGuiTest {
    
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
        var g0 = Gui.empty(2, 2);
        var g1 = Gui.empty(1, 3);
        var g2 = Gui.empty(3, 3);
        var b = new SlotElement.Item(Item.simple(ItemStack.of(Material.DIAMOND)));
        var gui = ScrollGui.guisBuilder()
            .setStructure(
                "b b b b b",
                "b . x x b",
                "b x . x b",
                "b x x . b",
                "b b b b b"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('b', b)
            .setContent(List.of(g0, g1, g2))
            .build();
        
        assertEquals(5, gui.getMaxLine());
        assertEquals(8, gui.getLineCount());
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, gl(g0, 1), null, b,
            b, gl(g0, 2), null, null, b,
            b, gl(g1, 0), null, null, b,
            b, b, b, b, b
        );
        
        gui.setLine(1);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, gl(g0, 3), null, b,
            b, gl(g1, 0), null, null, b,
            b, gl(g1, 1), null, null, b,
            b, b, b, b, b
        );
        
        gui.setLine(2);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, null, null, b,
            b, gl(g1, 1), null, null, b,
            b, gl(g1, 2), null, null, b,
            b, b, b, b, b
        );
        
        gui.setLine(3);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, null, null, b,
            b, gl(g1, 2), null, null, b,
            b, gl(g2, 0), gl(g2, 1), null, b,
            b, b, b, b, b
        );
        
        gui.setLine(4);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, null, null, b,
            b, gl(g2, 0), null, gl(g2, 2), b,
            b, gl(g2, 3), gl(g2, 4), null, b,
            b, b, b, b, b
        );
        
        gui.setLine(5);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, gl(g2, 1), gl(g2, 2), b,
            b, gl(g2, 3), null, gl(g2, 5), b,
            b, gl(g2, 6), gl(g2, 7), null, b,
            b, b, b, b, b
        );
    }
    
    @Test
    public void testWithNonContinuousVerticalLines() {
        var g0 = Gui.empty(2, 2);
        var g1 = Gui.empty(1, 3);
        var g2 = Gui.empty(3, 3);
        var b = new SlotElement.Item(Item.simple(ItemStack.of(Material.DIAMOND)));
        var gui = ScrollGui.guisBuilder()
            .setStructure(
                "b b b b b",
                "b . x x b",
                "b x . x b",
                "b x x . b",
                "b b b b b"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_VERTICAL)
            .addIngredient('b', b)
            .setContent(List.of(g0, g1, g2))
            .build();
        
        assertEquals(3, gui.getMaxLine());
        assertEquals(6, gui.getLineCount());
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, gl(g0, 1), gl(g1, 0), b,
            b, gl(g0, 2), null, gl(g1, 1), b,
            b, null, null, null, b,
            b, b, b, b, b
        );
        
        gui.setLine(1);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, gl(g1, 0), gl(g2, 0), b,
            b, gl(g0, 3), null, gl(g2, 3), b,
            b, null, gl(g1, 2), null, b,
            b, b, b, b, b
        );
        
        gui.setLine(2);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, gl(g2, 0), gl(g2, 1), b,
            b, gl(g1, 1), null, gl(g2, 4), b,
            b, gl(g1, 2), gl(g2, 6), null, b,
            b, b, b, b, b
        );
        
        gui.setLine(3);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, gl(g2, 1), gl(g2, 2), b,
            b, gl(g2, 3), null, gl(g2, 5), b,
            b, gl(g2, 6), gl(g2, 7), null, b,
            b, b, b, b, b
        );
    }
    
    @ValueSource(booleans = {true, false})
    @ParameterizedTest()
    public void testContentRemoved(boolean horizontalLines) {
        var m = horizontalLines ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL;
        var gui = ScrollGui.guisBuilder()
            .setStructure("x x x")
            .addIngredient('x', m)
            .setContent(List.of(Gui.empty(3, 1)))
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
    public void testScrollGuisInitWithBuilder() {
        testScrollGuisInit(content ->
            ScrollGui.guisBuilder()
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
    public void testPagedGuisInitWithStaticFactoryFunction() {
        testScrollGuisInit(content ->
            ScrollGui.ofGuis(
                3, 3,
                content,
                SlotUtils.toSlotList(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, 3),
                ScrollGui.LineOrientation.HORIZONTAL
            )
        );
    }
    
    private void testScrollGuisInit(Function<? super List<Gui>, ? extends Gui> createGui) {
        var content = List.of(Gui.empty(3, 1), Gui.empty(3, 1));
        
        var gui = createGui.apply(content);
        
        for (int i = 0; i < 6; i++) {
            var element = gui.getSlotElement(i);
            assertInstanceOf(SlotElement.GuiLink.class, element, "i=" + i);
            assertSame(content.get(i / 3), ((SlotElement.GuiLink) element).gui(), "i= " + i);
            assertEquals(i % 3, ((SlotElement.GuiLink) element).slot(), "i= " + i);
        }
        for (int i = 6; i < 9; i++) {
            assertNull(gui.getSlotElement(i), "i=" + i);
        }
    }
    
    
}
