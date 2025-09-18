package xyz.xenondevs.invui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;

import static org.junit.jupiter.api.Assertions.*;

public class Utils {
    
    public static void assertSlotElements(Gui gui, @Nullable SlotElement... elements) {
        assertEquals(gui.getSize(), elements.length);
        for (int i = 0; i < elements.length; i++) {
            assertEquals(elements[i], gui.getSlotElement(i), "i=" + i);
        }
    }
    
    public static void assertItems(Gui gui, @Nullable Item... items) {
        assertEquals(gui.getSize(), items.length);
        for (int i = 0; i < items.length; i++) {
            var se = gui.getSlotElement(i);
            if (items[i] == null) {
                assertNull(se, "i=" + i);
            } else {
                assertInstanceOf(SlotElement.Item.class, se, "i=" + i);
                assertEquals(items[i], ((SlotElement.Item) se).item(), "i=" + i);
            }
        }
    }
    
    public static SlotElement.InventoryLink il(Inventory inventory, int slot) {
        return new SlotElement.InventoryLink(inventory, slot);
    }
    
    public static SlotElement.GuiLink gl(Gui g, int slot) {
        return new SlotElement.GuiLink(g, slot);
    }
    
    public static SlotElement.GuiLink gl(Gui gui, int x, int y) {
        return new SlotElement.GuiLink(gui, gui.getWidth() * y + x);
    }
    
}
