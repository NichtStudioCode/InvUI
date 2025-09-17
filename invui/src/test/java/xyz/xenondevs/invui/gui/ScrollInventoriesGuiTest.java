package xyz.xenondevs.invui.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class ScrollInventoriesGuiTest {
    
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
    public void testScrollInventoriesInitWithBuilder() {
        testScrollInventoriesInit(content ->
            ScrollGui.inventoriesBuilder()
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
    public void testScrollInventoriesInitWithStaticFactoryFunction() {
        testScrollInventoriesInit(content ->
            ScrollGui.ofInventories(
                3, 3,
                content,
                SlotUtils.toSlotSet(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, 3),
                ScrollGui.ScrollDirection.VERTICAL
            )
        );
    }
    
    private void testScrollInventoriesInit(Function<? super List<Inventory>, ? extends Gui> createGui) {
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
