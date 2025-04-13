package xyz.xenondevs.invui.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.internal.util.SlotUtils;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

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
    
    @Test
    public void testPagedGuisInitWithBuilder() {
        testPagedGuisInit(content ->
            PagedGui.guis()
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
                SlotUtils.toSlotSet(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, 3)
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
