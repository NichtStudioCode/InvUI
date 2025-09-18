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

public class PagedGuisGuiTest {
    
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
    
    @ValueSource(booleans = {true, false})
    @ParameterizedTest()
    public void testWithNonContinuousLayout(boolean horizontalLines) {
        var m = horizontalLines ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL;
        var t1 = Gui.empty(3, 1);
        var t2 = Gui.empty(2, 5);
        var t3 = Gui.empty(3, 3);
        var b = new SlotElement.Item(Item.simple(ItemStack.of(Material.BLACK_STAINED_GLASS)));
        var gui = PagedGui.guisBuilder()
            .setStructure(
                "# # # # #",
                "# . x x #",
                "# x . x #",
                "# . x . #",
                "# # # # #"
            )
            .addIngredient('#', b)
            .addIngredient('x', m)
            .setContent(List.of(t1, t2, t3))
            .build();
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, gl(t1, 1, 0), gl(t1, 2, 0), b,
            b, null, null, null, b,
            b, null, null, null, b,
            b, b, b, b, b
        );
        
        gui.setPage(1);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, gl(t2, 1, 0), null, b,
            b, gl(t2, 0, 1), null, null, b,
            b, null, gl(t2, 1, 2), null, b,
            b, b, b, b, b
        );
        
        gui.setPage(2);
        
        assertSlotElements(
            gui,
            b, b, b, b, b,
            b, null, gl(t3, 1, 0), gl(t3, 2, 0), b,
            b, gl(t3, 0, 1), null, gl(t3, 2, 1), b,
            b, null, gl(t3, 1, 2), null, b,
            b, b, b, b, b
        );
    }
    
    @ValueSource(booleans = {true, false})
    @ParameterizedTest()
    public void testContentRemoved(boolean horizontalLines) {
        var m = horizontalLines ? Markers.CONTENT_LIST_SLOT_HORIZONTAL : Markers.CONTENT_LIST_SLOT_VERTICAL;
        var gui = PagedGui.guisBuilder()
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
    public void testPagedGuisInitWithBuilder() {
        testPagedGuisInit(content ->
            PagedGui.guisBuilder()
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
        testPagedGuisInit(content ->
            PagedGui.ofGuis(
                3, 3,
                content,
                SlotUtils.toSlotList(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, 3)
            )
        );
    }
    
    private void testPagedGuisInit(Function<? super List<Gui>, ? extends Gui> createGui) {
        var content = List.of(Gui.empty(3, 1), Gui.empty(3, 1));
        
        var gui = createGui.apply(content);
        
        for (int i = 0; i < 3; i++) {
            var element = gui.getSlotElement(i);
            assertInstanceOf(SlotElement.GuiLink.class, element, "i=" + i);
            assertSame(content.getFirst(), ((SlotElement.GuiLink) element).gui(), "i= " + i);
            assertEquals(i, ((SlotElement.GuiLink) element).slot(), "i= " + i);
        }
        for (int i = 3; i < 9; i++) {
            assertNull(gui.getSlotElement(i), "i=" + i);
        }
    }
    
}
