package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.item.Item;

import java.util.List;

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
        
        var gui = TabGui.normal()
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
